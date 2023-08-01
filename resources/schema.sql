-- This scheme represents the database structure

-- Drop tables that may already exist
DROP TABLE IF EXISTS `Apartment`;
DROP TABLE IF EXISTS `Room`;
DROP TABLE IF EXISTS `Customer`;
DROP TABLE IF EXISTS `Reservation`;

-- Create the tables
CREATE TABLE IF NOT EXISTS Apartment (
    id                  INT PRIMARY KEY,
    description         TEXT NOT NULL,
    maxGuestsAllowed    INT NOT NULL,
    numberOfRooms       INT,
    numberOfBathrooms   INT,
    numberOfBedrooms    INT,
    numberOfBeds        INT
);

CREATE TABLE IF NOT EXISTS Room (
    id                  INT PRIMARY KEY,
    description         TEXT NOT NULL,
    maxGuestsAllowed    INT NOT NULL,
    hasPrivateBathroom  BOOLEAN,
    hasKitchen          BOOLEAN
);

CREATE TABLE IF NOT EXISTS Customer (
    id                  INT PRIMARY KEY,
    name                TEXT NOT NULL,
    address             TEXT NOT NULL,
    phone               TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Reservation (
    id                  INT PRIMARY KEY,
    accommodationId     INT NOT NULL,
    arrivalDate         DATE NOT NULL,
    departureDate       DATE NOT NULL,
    numberOfGuests      INT NOT NULL,
    numberOfChildren    INT NOT NULL,
    customerId          INT NOT NULL,
    price               DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (accommodationId) REFERENCES Apartment(id),
    FOREIGN KEY (accommodationId) REFERENCES Room(id),
    FOREIGN KEY (customerId) REFERENCES Customer(id)
);

