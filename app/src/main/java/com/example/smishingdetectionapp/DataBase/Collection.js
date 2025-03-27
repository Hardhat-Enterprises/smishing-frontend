const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const bcrypt = require('bcryptjs');

const userSchema = new Schema({
    FullName: {
        type: String,
        required: true
    },
    PhoneNumber: {
        type: String,
        required: true
    },
    Email: {
        type: String,
        required: true
    },
    Password: {
        type: String,
        required: true
    },
    Code: { type: String },
    isVerified: { type: Boolean, default: false }
});


// Hashes the Passwords
userSchema.pre('save', async function(next) {

    if(!this.isModified('Password')){
        next()
    }
this.Password = await bcrypt.hash(this.Password, 10)
});

// Method to compare the provided password with the hashed password in the database
userSchema.methods.comparePassword = async function(candidatePassword) {
    return await bcrypt.compare(candidatePassword, this.Password);
};

const details = mongoose.model('details', userSchema);
module.exports = details;
