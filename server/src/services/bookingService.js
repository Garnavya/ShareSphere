const prisma = require('../config/db');

class BookingService {
  /**
   * Atomic Reservation Engine:
   * Validates date window [startDate, endDate] against existing active/confirmed bookings
   * within an ACID transaction to prevent double-booking race conditions.
   */
  static async createBookingAtomic({ itemId, borrowerId, startDate, endDate, fulfillmentType }) {
    const start = new Date(startDate);
    const end = new Date(endDate);

    if (start >= end) {
      const err = new Error('End date must be strictly after start date.');
      err.statusCode = 400;
      throw err;
    }

    // Execute within a Prisma ACID transaction
    return await prisma.$transaction(async (tx) => {
      // 1. Fetch item and verify availability
      const item = await tx.item.findUnique({
        where: { id: itemId }
      });

      if (!item || !item.isAvailable) {
        const err = new Error('Item is currently not available for borrowing.');
        err.statusCode = 404;
        throw err;
      }

      if (item.ownerId === borrowerId) {
        const err = new Error('You cannot borrow your own item.');
        err.statusCode = 400;
        throw err;
      }

      // 2. Strict Overlap Concurrency Check:
      // Overlap condition: (startDate <= requestedEnd AND endDate >= requestedStart)
      // for any booking that is CONFIRMED, AWAITING_HANDOVER, or ACTIVE
      const conflictingBooking = await tx.booking.findFirst({
        where: {
          itemId: itemId,
          status: {
            in: ['CONFIRMED', 'AWAITING_HANDOVER', 'ACTIVE']
          },
          startDate: { lte: end },
          endDate: { gte: start }
        }
      });

      if (conflictingBooking) {
        const err = new Error('Requested dates overlap with an existing confirmed booking.');
        err.statusCode = 409; // HTTP 409 Conflict
        throw err;
      }

      // 3. Compute duration and cost
      const diffTime = Math.abs(end - start);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      const dailyRate = item.isFree ? 0 : Number(item.dailyRate);
      const totalAmount = diffDays * dailyRate;

      // 4. Create booking record atomically
      const newBooking = await tx.booking.create({
        data: {
          itemId,
          borrowerId,
          startDate: start,
          endDate: end,
          totalDays: diffDays,
          totalAmount,
          status: 'PENDING',
          fulfillmentType: fulfillmentType || 'SELF_PICKUP'
        },
        include: {
          item: {
            select: { title: true, dailyRate: true, isFree: true, address: true }
          }
        }
      });

      return newBooking;
    });
  }
}

module.exports = BookingService;
