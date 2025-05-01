# lightweight-ph4ntoms

## Giới thiệu
`lightweight-ph4ntoms` là một hệ thống microservices được thiết kế để cung cấp các chức năng quản lý cấu hình, khám phá dịch vụ, xác thực và gửi email. Dự án được xây dựng trên nền tảng **Spring Boot** và sử dụng các công cụ như **Spring Cloud**, **Netflix Eureka**, **Redis**, và **Kafka** để đảm bảo tính linh hoạt, khả năng mở rộng và dễ dàng tích hợp.

Hệ thống bao gồm bốn service chính:
- **Configuration Service**: Quản lý cấu hình tập trung.
- **Discovery Service**: Đăng ký và khám phá các dịch vụ trong hệ thống.
- **Authenticate Service**: Xử lý xác thực và phân quyền người dùng.
- **Message Service**: Gửi email và hỗ trợ giao tiếp không đồng bộ qua Kafka.

---

## Các Service

### 1. Configuration Service
- **Mô tả**:  
  Đây là một **Spring Cloud Config Server**, cung cấp cấu hình tập trung cho toàn bộ hệ thống. Thay vì lưu trữ cấu hình riêng lẻ trong từng service, tất cả cấu hình được quản lý tại một nơi duy nhất, giúp dễ dàng cập nhật và đồng bộ.

- **Tính năng chính**:
  - Quản lý cấu hình tập trung.
  - Hỗ trợ cập nhật cấu hình động mà không cần khởi động lại service.
  - Hỗ trợ nhiều môi trường (ví dụ: `dev`, `prod`).

- **Cách hoạt động**:
  - Khi một service khởi động, nó sẽ gửi yêu cầu đến Configuration Service để lấy cấu hình tương ứng với profile hiện tại.
  - Configuration Service có thể lấy cấu hình từ các nguồn như tệp cục bộ hoặc Git repository.

- **Cấu hình**:
  - **Port**: `8888`
  - **Nguồn cấu hình**: `classpath:/configurations` (cục bộ) hoặc Git repository.
  - **Profiles**: `native` (cấu hình cục bộ) hoặc `git`.

---

### 2. Discovery Service
- **Mô tả**:  
  Đây là một **Netflix Eureka Server**, chịu trách nhiệm quản lý việc đăng ký và khám phá các service trong hệ thống. Nó giúp các service giao tiếp với nhau mà không cần biết trước địa chỉ cụ thể.

- **Tính năng chính**:
  - Đăng ký và khám phá dịch vụ.
  - Theo dõi trạng thái của các service (UP/DOWN).
  - Hỗ trợ cân bằng tải khi tích hợp với các công cụ như Ribbon hoặc Load Balancer.

- **Cách hoạt động**:
  - Khi một service khởi động, nó sẽ tự động đăng ký với Discovery Service.
  - Các service khác có thể gửi yêu cầu đến Discovery Service để lấy thông tin về các service đã đăng ký.

- **Cấu hình**:
  - **Port**: `8761`
  - **Giao diện web**: `http://localhost:8761` (hiển thị danh sách các service đã đăng ký).
  - **Self-registration**: `registerWithEureka: false` (Discovery Service không tự đăng ký chính nó).

---

### 3. Authenticate Service
- **Mô tả**:  
  Đây là một service chịu trách nhiệm xử lý xác thực và phân quyền người dùng trong hệ thống. Nó sử dụng **Spring Security** và **Redis** để quản lý phiên đăng nhập và token.

- **Tính năng chính**:
  - Xác thực người dùng (Authentication)
  - Phân quyền (Authorization)
  - Quản lý phiên đăng nhập với Redis
  - Tích hợp JWT cho bảo mật

- **Cách hoạt động**:
  - Xử lý các yêu cầu đăng nhập và xác thực
  - Lưu trữ và quản lý thông tin phiên trong Redis
  - Cung cấp các endpoint để kiểm tra và xác thực token

- **Cấu hình**:
  - **Port**: `8765`
  - **Redis**: Sử dụng cho lưu trữ phiên
  - **JWT**: Quản lý token xác thực

---

### 4. Message Service
- **Mô tả**:  
  Đây là một service chịu trách nhiệm gửi email và hỗ trợ giao tiếp không đồng bộ qua Kafka. Nó sử dụng **Spring Boot Mail Starter** để tích hợp với các máy chủ SMTP.

- **Tính năng chính**:
  - Gửi email thông qua các máy chủ SMTP (ví dụ: Gmail).
  - Hỗ trợ Kafka producer để gửi thông báo không đồng bộ.
  - Có thể mở rộng để tích hợp với các hệ thống nhắn tin khác.

- **Cách hoạt động**:
  - Khi nhận được yêu cầu gửi email, Message Service sẽ sử dụng cấu hình SMTP để gửi email đến người nhận.
  - Nếu tích hợp Kafka, các thông báo sẽ được gửi đến Kafka topic để xử lý không đồng bộ.

- **Cấu hình**:
  - **Port**: `8767`
  - **SMTP**:
    - Host: `smtp.gmail.com`
    - Port: `587`
    - Authentication: `true`
    - StartTLS: `true`
  - **Kafka**:
    - Producer được cấu hình để gửi thông báo đến Kafka topic.

---

## Yêu cầu hệ thống
- **Java**: JDK 17 hoặc cao hơn
- **Maven**: 3.9.9 hoặc cao hơn
- **Docker**: Để chạy các dịch vụ phụ thuộc
- **Docker Compose**: Để quản lý các container

### Các dịch vụ phụ thuộc (được cung cấp qua Docker)
- **PostgreSQL**: Cơ sở dữ liệu chính
- **Redis**: Lưu trữ phiên và cache
- **RedisInsight**: Giao diện quản lý Redis (port 5540)
- **Kafka**: Message broker
- **Zookeeper**: Quản lý Kafka cluster
- **Zipkin**: Distributed tracing (port 9411)

---

## Khởi động hệ thống
1. Đảm bảo Docker và Docker Compose đã được cài đặt
2. Chạy lệnh sau để khởi động các dịch vụ phụ thuộc:
   ```bash
   docker-compose up -d
   ```
3. Khởi động các service theo thứ tự:
   - Configuration Service
   - Discovery Service
   - Authenticate Service
   - Message Service

---
