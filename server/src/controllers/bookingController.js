const prisma = require('../config/db');
const BookingService = require('../services/bookingService');
const OtpService = require('../services/otpService');

exports.requestBooking = async (req, res, next) => {
  try {
    const { itemId, startDate, endDate, fulfillmentType } = req.body;

    if (!itemId || !startDate || !endDate) {
      return res.status(400).json({ success: false, message: 'Item ID, start date, and end date are required.' });
    }

    const booking = await BookingService.createBookingAtomic({
      itemId,
      borrowerId: req.user.id,
      startDate,
      endDate,
      fulfillmentType
    });

    res.status(201).json({
      success: true,
      message: 'Borrow request submitted successfully and awaiting lender approval.',
      booking
    });
  } catch (error) {
    next(error);
  }
};

exports.respondToBooking = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { action } = req.body; // 'ACCEPT' or 'REJECT'

    const booking = await prisma.booking.findUnique({
      where: { id },
      include: { item: true }
    });

    if (!booking) {
      return res.status(404).json({ success: false, message: 'Booking not found.' });
    }

    if (booking.item.ownerId !== req.user.id) {
      return res.status(403).json({ success: false, message: 'Only the item owner can respond to this booking request.' });
    }

    if (booking.status !== 'PENDING') {
      return res.status(400).json({ success: false, message: `Cannot ${action} a booking that is ${booking.status}.` });
    }

    if (action === 'ACCEPT') {
      // Generate OTP for physical handover verification
      const { rawOtp, hashedOtp } = await OtpService.generateOtp();

      const updated = await prisma.booking.update({
        where: { id },
        data: {
          status: 'AWAITING_HANDOVER',
          pickupOtp: hashedOtp
        }
      });

      return res.status(200).json({
        success: true,
        message: 'Booking request accepted. Handover OTP generated.',
        booking: updated,
        pickupOtpPreview: rawOtp // Sent securely to borrower in production notification
      });
    } else if (action === 'REJECT') {
      const updated = await prisma.booking.update({
        where: { id },
        data: { status: 'REJECTED' }
      });

      return res.status(200).json({
        success: true,
        message: 'Booking request rejected.',
        booking: updated
      });
    } else {
      return res.status(400).json({ success: false, message: "Invalid action. Use 'ACCEPT' or 'REJECT'." });
    }
  } catch (error) {
    next(error);
  }
};

exports.verifyHandoverOtp = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { otp, stage } = req.body; // stage: 'PICKUP' or 'RETURN'

    const booking = await prisma.booking.findUnique({
      where: { id },
      include: { item: true }
    });

    if (!booking) {
      return res.status(404).json({ success: false, message: 'Booking not found.' });
    }

    if (stage === 'PICKUP') {
      if (booking.status !== 'AWAITING_HANDOVER') {
        return res.status(400).json({ success: false, message: 'Booking is not awaiting handover.' });
      }

      const isValid = await OtpService.verifyOtp(otp, booking.pickupOtp);
      if (!isValid) {
        return res.status(400).json({ success: false, message: 'Invalid or expired Pickup OTP.' });
      }

      const updated = await prisma.booking.update({
        where: { id },
        data: {
          status: 'ACTIVE',
          pickupOtp: null // Clear used OTP
        }
      });

      return res.status(200).json({
        success: true,
        message: 'Pickup verified successfully. Booking is now ACTIVE.',
        booking: updated
      });
    }

    res.status(400).json({ success: false, message: 'Invalid handover stage requested.' });
  } catch (error) {
    next(error);
  }
};

exports.getMyBorrowings = async (req, res, next) => {
  try {
    const bookings = await prisma.booking.findMany({
      where: { borrowerId: req.user.id },
      include: {
        item: {
          include: { images: true, owner: { select: { id: true, name: true, phone: true } } }
        }
      },
      orderBy: { createdAt: 'desc' }
    });

    res.status(200).json({ success: true, count: bookings.length, bookings });
  } catch (error) {
    next(error);
  }
};
