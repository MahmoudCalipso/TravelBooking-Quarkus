#!/bin/bash
# ============================================
# JWT RSA Key Pair Generation Script
# Travel Platform Backend
# ============================================

echo "Generating RSA key pair for JWT authentication..."

# Generate private key (2048 bits)
openssl genrsa -out privateKey.pem 2048

# Generate public key from private key
openssl rsa -in privateKey.pem -pubout -out publicKey.pem

# Set appropriate permissions
chmod 600 privateKey.pem
chmod 644 publicKey.pem

echo "✅ JWT keys generated successfully!"
echo "📁 Files created:"
echo "   - privateKey.pem (keep this SECRET!)"
echo "   - publicKey.pem (can be shared)"
echo ""
echo "⚠️  IMPORTANT SECURITY NOTES:"
echo "   1. NEVER commit privateKey.pem to version control"
echo "   2. Add privateKey.pem to .gitignore"
echo "   3. In production, load keys from environment variables or secret management service"
echo "   4. Rotate keys periodically (every 6-12 months)"
echo ""
echo "📝 Update your application.properties if needed:"
echo "   quarkus.smallrye-jwt.rsa-public-key.location=publicKey.pem"
echo "   quarkus.smallrye-jwt.rsa-private-key.location=privateKey.pem"
