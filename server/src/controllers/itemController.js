const prisma = require('../config/db');

exports.getItems = async (req, res, next) => {
  try {
    const { category, search, isFree } = req.query;

    const where = { isAvailable: true };
    if (category) {
      where.category = category;
    }
    if (isFree === 'true') {
      where.isFree = true;
    }
    if (search) {
      where.OR = [
        { title: { contains: search } },
        { description: { contains: search } }
      ];
    }

    const items = await prisma.item.findMany({
      where,
      include: {
        images: true,
        owner: {
          select: { id: true, name: true, rating: true, avatarUrl: true }
        }
      },
      orderBy: { createdAt: 'desc' }
    });

    res.status(200).json({ success: true, count: items.length, items });
  } catch (error) {
    next(error);
  }
};

exports.getItemById = async (req, res, next) => {
  try {
    const { id } = req.params;

    const item = await prisma.item.findUnique({
      where: { id },
      include: {
        images: true,
        owner: {
          select: { id: true, name: true, rating: true, ratingCount: true, avatarUrl: true, address: true }
        },
        bookings: {
          where: {
            status: { in: ['CONFIRMED', 'AWAITING_HANDOVER', 'ACTIVE'] }
          },
          select: { startDate: true, endDate: true }
        }
      }
    });

    if (!item) {
      return res.status(404).json({ success: false, message: 'Item not found.' });
    }

    res.status(200).json({ success: true, item });
  } catch (error) {
    next(error);
  }
};

exports.createItem = async (req, res, next) => {
  try {
    const { title, description, category, condition, dailyRate, securityDeposit, isFree, address, imageUrls } = req.body;

    if (!title || !category || !address) {
      return res.status(400).json({ success: false, message: 'Title, category, and address are required.' });
    }

    const item = await prisma.item.create({
      data: {
        title,
        description,
        category,
        condition: condition || 'GOOD',
        dailyRate: isFree ? 0 : Number(dailyRate || 0),
        securityDeposit: Number(securityDeposit || 0),
        isFree: Boolean(isFree),
        address,
        ownerId: req.user.id,
        images: imageUrls && imageUrls.length > 0 ? {
          create: imageUrls.map((url, idx) => ({
            url,
            isPrimary: idx === 0
          }))
        } : undefined
      },
      include: { images: true }
    });

    res.status(201).json({ success: true, message: 'Item listing created successfully', item });
  } catch (error) {
    next(error);
  }
};
