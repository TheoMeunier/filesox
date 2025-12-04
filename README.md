<div align="center">
<a href="https://github.com/TheoMeunier/Filesox">
<img src="doc/images/logo.svg" alt="Logo" width="100" height="100">
</a>

<h2 align="center">Filesox</h3>
  <p align="center">
    <a href="https://github.com/TheoMeunier/Filesox/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    ·
    <a href="https://github.com/TheoMeunier/Filesox/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>

## About The Project

FileManager S3 is a versatile file manager that offers a flexible storage solution, enabling the use of Amazon S3.
Featuring an intuitive user interface and robust administration panel, it aims to simplify file
management for businesses and developers.

<img src="doc/images/demo-filesox.png" alt="demo-filesox" width="100%" height="auto">

## Getting Started

### Prerequisites

* nodejs lts
* java 21
* postgres 17.4

### Installation for development

1. Clone the repo
   ```sh
   git clone https://github.com/TheoMeunier/filesox.git
   ```
2. Configuring the `variable environnement` file

3. Build front-end
   ```sh 
    cd front
    npm install
    npm run dev
   ```
4. Build back-end
   ```sh
    ./gradlew quarkusDev
   ```
5. Run docker compose
   ```sh
    docker compose up -d
   ```

#### Docker

### 1. Create keys for JWT token with `openssl`:

```bash
mkdir certs/ && cd certs

openssl genrsa -out rsaPrivateKey.pem 2048
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem

chmod 644 privateKey.pem publicKey.pem
```

2. Create a `compose.yaml` file

```yml
services:
  # Web Service
  front:
    image: ghcr.io/theomeunier/filesox/front:latest
    container_name: filesox_front
    restart: unless-stopped
    ports:
      - "80:80"
    networks:
      - app_network

  # Api Service
  back:
    image: ghcr.io/theomeunier/filesox/api-native:latest
    container_name: filesox_api
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      QUARKUS_DATASOURCE_USERNAME: filesox
      QUARKUS_DATASOURCE_PASSWORD: filesox
      QUARKUS_DATASOURCE_JDBC_URL: jdbc:postgresql://filesox_database/filesox
      QUARKUS_S3_ENDPOINT_OVERRIDE:
      QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_ACCESS_KEY_ID:
      QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_SECRET_ACCESS_KEY:
      QUARKUS_S3_BUCKET: filesox
      QUARKUS_S3_AWS_REGION: eu-west-1
      QUARKUS_REDIS_HOSTS: redis://:filesox-redis@redis:6379
      MP_JWT_VERIFY_PUBLICKEY_LOCATION: /certs/publicKey.pem
      SMALLRYE_JWT_SIGN_KEY_LOCATION: /certs/privateKey.pem
    volumes:
      - ./certs:/certs
    networks:
      - app_network

  # Reverse Proxy Service
  nginx:
    image: nginx:alpine
    container_name: filesox_nginx
    restart: unless-stopped
    ports:
      - "8888:80"
    volumes:
      - ./docker/nginx/nginx.conf:/etc/nginx/conf.d/default.conf
    networks:
      - app_network

  # Database Service
  postgres:
    image: postgres:17.4-alpine
    container_name: filesox_database
    restart: unless-stopped
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: filesox
      POSTGRES_USER: filesox
      POSTGRES_PASSWORD: filesox
      PGDATA: /var/lib/postgresql/data/pgdata
    volumes:
      - ./storage-db:/var/lib/postgresql/data
    networks:
      - app_network

  # Redis Service
  redis:
    image: redis:latest
    container_name: filesox_redis
    restart: unless-stopped
    command: redis-server --requirepass filesox-redis
    ports:
      - "6379:6379"
    environment:
      REDIS_PASSWORD: filesox-redis
    networks:
      - app_network

networks:
  app_network:
    driver: bridge

```

### 3. Configure the `variable environnement` file

#### 3.1 PostgreSQL Configuration:

- `QUARKUS_DATASOURCE_USERNAME` : The username of your PostgreSQL database
- `QUARKUS_DATASOURCE_PASSWORD` : The password of your PostgreSQL database
- `QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://[host][:port][/database]` : The URL of your PostgreSQL database

#### 3.2 S3 Configuration:

- `QUARKUS_S3_ENDPOINT_OVERRIDE` : Endpoint of your S3 provider
- `QUARKUS_S3_BUCKET` : The bucket name of your S3 provider
- `QUARKUS_S3_AWS_REGION` : The region of your S3 provider
- `QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_ACCESS_KEY_ID` : Access key of your S3 provider
- `QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_SECRET_ACCESS_KEY` : Secret key of your S3 provider

#### 3.3 Redis Configuration;

- `QUARKUS_REDIS_HOSTS=redis://[username:password@][host][:port][/database]` : The URL of your Redis database

#### 3.4 JWT Configuration:

- `MP_JWT_VERIFY_PUBLICKEY_LOCATION` : The location of the public key used to verify the JWT token
- `SMALLRYE_JWT_SIGN_KEY_LOCATION` : The location of the private key used to sign the JWT token

### 4. Start the application with docker-compose

```bash
docker compose up -d
```

### 5. Access the application

```bash
http://localhost:8888
```

#### Connect to the application

Your default admin credentials are:

- Email: `admin@filesox.fr`
- Password: `adminadmin`

It is strongly recommended that you change this password immediately after your first login for security reasons.

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also
simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.