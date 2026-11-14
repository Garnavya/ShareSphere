const express = require('express');
const router = express.Router();
const itemController = require('../controllers/itemController');
const authenticate = require('../middlewares/authMiddleware');

router.get('/', itemController.getItems);
router.get('/:id', itemController.getItemById);
router.post('/', authenticate, itemController.createItem);

module.exports = router;
