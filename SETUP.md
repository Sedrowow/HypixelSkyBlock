# HypixelSkyBlock Setup Guide

This guide will help you set up the HypixelSkyBlock server from scratch.

## Prerequisites

- **Java 21** or higher (with `--enable-preview` flag support)
- **Docker Desktop** installed and running
- **Git** for version control
- **Gradle** (included via wrapper scripts)

## Step 1: Clone the Repository

```bash
git clone https://github.com/Sedrowow/HypixelSkyBlock.git
cd HypixelSkyBlock
```

## Step 2: Initial Build

Build all the project modules:

```bash
# On Windows
./gradlew.bat build -x test

# On Linux/Mac
./gradlew build -x test
```

This will compile all the Java code and create the necessary JAR files in each module's `build/libs/` directory.

## Step 3: Prepare World Files

If you don't have the `world.zip` file yet, you'll need to create the world templates:

### Option A: Use Existing Worlds (Recommended)

1. Check if `configuration/world.zip` exists
2. If it does, you're all set! Skip to Step 4.

### Option B: Create New Worlds

1. Create two Minecraft worlds:
   - **hypixel_hub**: The main hub world where players spawn
   - **hypixel_island_template**: The template for player islands

2. Place these world folders in the `configuration/` directory:
   ```
   configuration/
   ├── hypixel_hub/
   │   ├── region/
   │   ├── level.dat
   │   └── ...
   └── hypixel_island_template/
       ├── region/
       ├── level.dat
       └── ...
   ```

3. Create a zip file containing both worlds:
   ```bash
   cd configuration
   # On Windows (PowerShell)
   Compress-Archive -Path hypixel_hub,hypixel_island_template -DestinationPath world.zip
   
   # On Linux/Mac
   zip -r world.zip hypixel_hub hypixel_island_template
   ```

## Step 4: Configure MongoDB Initialization

The server uses MongoDB to store player data, islands, and profiles. Initial data files are located in:

```
configuration/skyblock/
├── Minestom.crystals.csv
├── Minestom.fairysouls.csv
├── Minestom.regions.csv
├── collections/      # Collection definitions
├── items/           # Item definitions
├── levels/          # Level progression
├── reforges/        # Item reforge stats
└── songs/           # In-game music
```

These files are automatically loaded when the MongoDB container starts.

## Step 5: Configure Velocity Proxy

Edit `configuration/velocity.toml` if you need to customize:
- Server port (default: 25565)
- Forwarding secret (for security between proxy and game servers)
- Player info forwarding mode

Edit `configuration/forwarding.secret` to set a secure secret key for server communication.

## Step 6: Build Docker Images

Build the Docker images that will run your servers:

```bash
docker-compose build game_server_builder
```

This creates the `game_server_prepared` image with all your compiled JARs and configuration files.

## Step 7: Start All Services

Start all containers (MongoDB, Redis, Proxy, Game Servers, Services):

```bash
docker-compose up -d
```

This will start:
- **mongodb**: Database for persistent storage
- **redis**: Cache and messaging
- **proxy**: Velocity proxy (port 25565)
- **hypixelcore_island**: SkyBlock island server
- **hypixelcore_hub**: Hub server
- **hypixelcore_farming**: Farming islands server
- **prototype_lobby**: Prototype lobby server
- **service_api**: API service
- **service_auctionhouse**: Auction house service
- **service_bazaar**: Bazaar service
- **service_itemtracker**: Item tracking service
- **service_party**: Party system service

## Step 8: Verify Services are Running

Check the status of all containers:

```bash
docker-compose ps
```

All services should show as "Up" or "healthy".

View logs for a specific service:

```bash
# View island server logs
docker-compose logs -f hypixelcore_island

# View proxy logs
docker-compose logs -f proxy

# View all logs
docker-compose logs -f
```

## Step 9: Connect to the Server

1. Open Minecraft (Java Edition, version 1.21 or compatible)
2. Add a multiplayer server with address: `localhost:25565`
3. Join the server!

## Common Issues and Solutions

### Docker Build Fails

**Problem**: `COPY ./loader/build/libs/HypixelCore.jar` fails with "no such file"

**Solution**: Run the Gradle build first:
```bash
./gradlew.bat build -x test
```

### Services Don't Start

**Problem**: Containers exit immediately or don't become healthy

**Solution**: Check logs for the specific service:
```bash
docker-compose logs [service_name]
```

Common causes:
- MongoDB not ready: Wait for MongoDB health check to pass
- Port conflicts: Make sure ports 25565, 27017, 6379 are available
- Missing configuration files: Verify `configuration/` directory has all required files

### World Not Loading

**Problem**: Players spawn in void or world is empty

**Solution**: 
1. Verify `world.zip` exists in `configuration/` directory
2. Check that the zip contains both `hypixel_hub` and `hypixel_island_template` folders
3. Rebuild the Docker image: `docker-compose build --no-cache game_server_builder`
4. Recreate containers: `docker-compose up -d --force-recreate`

### MongoDB Connection Failed

**Problem**: Server logs show "Connection refused" for MongoDB

**Solution**:
1. Check MongoDB is running: `docker-compose ps mongodb`
2. Wait for MongoDB to be healthy: `docker-compose logs mongodb`
3. Restart the game server: `docker-compose restart hypixelcore_island`

### Player Data Not Saving

**Problem**: Progress resets after disconnect or restart

**Solution**: This should be fixed in the current version. Verify:
1. Islands save on disconnect
2. Islands save on shutdown hook
3. Data is written to MongoDB (check with `docker-compose logs mongodb`)

If issues persist, check the game server logs for error messages.

## Development Workflow

### Making Code Changes

1. Edit the Java source files
2. Rebuild the project:
   ```bash
   ./gradlew.bat build -x test
   ```
3. Rebuild the Docker image:
   ```bash
   docker-compose build game_server_builder
   ```
4. Recreate the affected containers:
   ```bash
   docker-compose up -d --force-recreate hypixelcore_island hypixelcore_hub
   ```

### Viewing Live Logs

```bash
# Follow logs for island server
docker-compose logs -f hypixelcore_island

# Follow logs for all services
docker-compose logs -f
```

### Stopping Services

```bash
# Stop all services
docker-compose down

# Stop specific service
docker-compose stop hypixelcore_island

# Stop and remove all containers (keeps data)
docker-compose down

# Stop and remove all containers AND volumes (deletes database)
docker-compose down -v
```

### Resetting the Database

To completely reset all player data and islands:

```bash
# Stop all services
docker-compose down

# Remove the MongoDB volume
docker volume rm mongo-data

# Restart everything
docker-compose up -d
```

## Project Structure

```
HypixelSkyBlock/
├── configuration/          # Config files and world data
│   ├── world.zip          # World files (hub + island template)
│   ├── velocity.toml      # Proxy configuration
│   ├── forwarding.secret  # Security key
│   └── skyblock/          # Game data (items, collections, etc.)
├── loader/                # Main server loader
├── type.island/           # Island server type
├── type.hub/              # Hub server type
├── type.skyblockgeneric/  # Shared SkyBlock logic
├── service.api/           # API microservice
├── service.auctionhouse/  # Auction house microservice
├── service.bazaar/        # Bazaar microservice
├── DockerFiles/           # Docker build files
├── docker-compose.yml     # Docker orchestration
└── build.gradle.kts       # Root Gradle build file
```

## Port Reference

- **25565**: Minecraft server (Velocity proxy)
- **27017**: MongoDB database
- **6379**: Redis cache
- **20000+**: Internal game server ports (managed by proxy)

## Additional Resources

- [Main README](README.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [License](LICENSE)
- [This Repository](https://github.com/Sedrowow/HypixelSkyBlock)
- [Original Repository](https://github.com/Swofty-Developments/HypixelSkyBlock)

## Getting Help

If you encounter issues not covered in this guide:

1. Check the [Issues](https://github.com/Sedrowow/HypixelSkyBlock/issues) page on this fork
2. Check the [Original Repository Issues](https://github.com/Swofty-Developments/HypixelSkyBlock/issues) for upstream issues
3. Search for similar problems in closed issues
4. Create a new issue with:
   - Detailed description of the problem
   - Steps to reproduce
   - Relevant log outputs
   - Your environment (OS, Java version, Docker version)

## Contributing

Want to contribute? Great! Here's how:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Test thoroughly
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to your fork: `git push origin feature/amazing-feature`
7. Open a Pull Request

## License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.
