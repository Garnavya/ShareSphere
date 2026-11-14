const express = require('express');
const router = express.Router();
const bookingController = require('../controllers/bookingController');
const authenticate = require('../middlewares/authMiddleware');

router.post('/', authenticate, bookingController.requestBooking);
router.get('/my-borrowings', authenticate, bookingController.getMyBorrowings);
router.patch('/:id/respond', authenticate, bookingController.respondToBooking);
router.post('/:id/verify-otp', authenticate, bookingController.verifyHandoverOtp);

module.exports = router;
