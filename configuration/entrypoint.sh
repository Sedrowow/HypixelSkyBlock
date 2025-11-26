#!/bin/bash
set -e

# Read the forwarding secret
secret=$(cat /app/configuration_files/forwarding.secret)

# Update resources.json with the forwarding secret
jq --arg secret "$secret" '.["velocity-secret"] = $secret' /app/configuration/resources.json > /app/configuration/resources.json.tmp
mv /app/configuration/resources.json.tmp /app/configuration/resources.json

# Replace the secret in settings.yml for NanoLimbo
sed -i "s/secret: '.*'/secret: '$secret'/" /app/settings.yml

# Set the settings.yml bind: ip: 'localhost' to bind: ip: '0.0.0.0'
sed -i "s/ip: 'localhost'/ip: '0.0.0.0'/" /app/settings.yml

echo "Starting: $SERVICE_CMD"
exec $SERVICE_CMD