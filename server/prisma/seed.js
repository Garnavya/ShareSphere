const { PrismaClient } = require('@prisma/client');
const bcrypt = require('bcryptjs');

const prisma = new PrismaClient();

async function main() {
  console.log('Clearing existing records...');
  await prisma.review.deleteMany({});
  await prisma.conditionLog.deleteMany({});
  await prisma.deliveryTask.deleteMany({});
  await prisma.booking.deleteMany({});
  await prisma.itemImage.deleteMany({});
  await prisma.item.deleteMany({});
  await prisma.user.deleteMany({});

  console.log('Seeding demo users...');
  const salt = await bcrypt.genSalt(10);
  const passwordHash = await bcrypt.hash('Password123!', salt);

  const admin = await prisma.user.create({
    data: {
      name: 'System Admin',
      email: 'admin@sharesphere.local',
      passwordHash,
      role: 'ADMIN',
      address: 'Central Campus Administration Block'
    }
  });

  const lender = await prisma.user.create({
    data: {
      name: 'Priya Sharma',
      email: 'priya@sharesphere.local',
      passwordHash,
      phone: '+919876543210',
      role: 'USER',
      address: 'Hostel Block 3, North Campus',
      rating: 4.9,
      ratingCount: 14
    }
  });

  const borrower = await prisma.user.create({
    data: {
      name: 'Arjun Verma',
      email: 'arjun@sharesphere.local',
      passwordHash,
      phone: '+919876543211',
      role: 'USER',
      address: 'Sector 15, Green Park Apartments',
      rating: 4.8,
      ratingCount: 8
    }
  });

  const partner = await prisma.user.create({
    data: {
      name: 'Rohan Mehta',
      email: 'rohan@sharesphere.local',
      passwordHash,
      phone: '+919876543212',
      role: 'DELIVERY_PARTNER',
      address: 'Campus Student Center',
      rating: 5.0,
      ratingCount: 22
    }
  });

  console.log('Seeding items...');
  const item1 = await prisma.item.create({
    data: {
      title: 'Bosch Professional Cordless Drill (18V)',
      description: 'Heavy duty drill machine with two lithium batteries, charging dock, and standard bit set. Perfect for DIY furniture and wall mounting.',
      category: 'Power Tools',
      condition: 'LIKE_NEW',
      dailyRate: 150.00,
      securityDeposit: 500.00,
      address: 'Hostel Block 3, North Campus',
      ownerId: lender.id,
      images: {
        create: [
          {
            url: 'https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=800&q=80',
            isPrimary: true
          }
        ]
      }
    }
  });

  const item2 = await prisma.item.create({
    data: {
      title: 'Sony Alpha A6400 Mirrorless Camera + 16-50mm Lens',
      description: '4K video capable camera with fast autofocus. Includes memory card, 2 batteries, and camera bag.',
      category: 'Electronics & Photography',
      condition: 'BRAND_NEW',
      dailyRate: 600.00,
      securityDeposit: 2000.00,
      address: 'Hostel Block 3, North Campus',
      ownerId: lender.id,
      images: {
        create: [
          {
            url: 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80',
            isPrimary: true
          }
        ]
      }
    }
  });

  const item3 = await prisma.item.create({
    data: {
      title: 'Database System Concepts (Silberschatz, 7th Edition)',
      description: 'Essential textbook for CS course. Clean copy with no markings.',
      category: 'Academic Books',
      condition: 'GOOD',
      dailyRate: 0.00,
      isFree: true,
      securityDeposit: 100.00,
      address: 'Hostel Block 3, North Campus',
      ownerId: lender.id,
      images: {
        create: [
          {
            url: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80',
            isPrimary: true
          }
        ]
      }
    }
  });

  console.log(`Database seeded successfully with Users: ${admin.email}, ${lender.email}, ${borrower.email}, ${partner.email}`);
}

main()
  .catch((e) => {
    console.error('Seed error:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
