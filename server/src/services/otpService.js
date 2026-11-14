const crypto = require('crypto');
const bcrypt = require('bcryptjs');

class OtpService {
  /**
   * Generates a 6-digit numeric OTP and its salted bcrypt hash
   */
  static async generateOtp() {
    // Generate secure random integer between 100000 and 999999
    const rawOtp = crypto.randomInt(100000, 1000000).toString();
    const salt = await bcrypt.genSalt(10);
    const hashedOtp = await bcrypt.hash(rawOtp, salt);

    return { rawOtp, hashedOtp };
  }

  /**
   * Validates an incoming plain-text OTP against the stored hashed OTP
   */
  static async verifyOtp(rawOtp, hashedOtp) {
    if (!rawOtp || !hashedOtp) {
      return false;
    }
    return await bcrypt.compare(rawOtp, hashedOtp);
  }
}

module.exports = OtpService;
