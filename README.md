# lightweight-ph4ntoms

## Giới thiệu
Dự án `lightweight-ph4ntoms` là một hệ thống microservices được xây dựng để cung cấp các chức năng quản lý cấu hình và khám phá dịch vụ. Hệ thống này bao gồm các service chính như **Configuration Service** và **Discovery Service**, được thiết kế để hoạt động đồng bộ nhằm hỗ trợ các ứng dụng khác trong hệ sinh thái.

## Các Service

### 1. Configuration Service
- **Mô tả**: 
  Đây là một **Spring Cloud Config Server**, đóng vai trò là trung tâm quản lý cấu hình cho toàn bộ hệ thống. Thay vì lưu trữ cấu hình riêng lẻ trong từng service, Configuration Service cung cấp một cách tiếp cận tập trung, giúp dễ dàng quản lý và thay đổi cấu hình mà không cần khởi động lại các service.

- **Cách hoạt động**:
  - Configuration Service đọc các file cấu hình từ một nguồn lưu trữ (có thể là thư mục cục bộ, Git repository, hoặc cơ sở dữ liệu).
  - Các service khác sẽ gửi yêu cầu HTTP đến Configuration Service để lấy cấu hình tương ứng với môi trường (ví dụ: `dev`, `prod`).

- **Cấu hình chính**:
  - **Port**: Service này chạy trên cổng `8888`.
  - **Nguồn cấu hình**: Trong dự án này, cấu hình được lưu trữ tại thư mục `classpath:/configurations`.
  - **Hỗ trợ profile**: Configuration Service hỗ trợ nhiều profile như `native` (cấu hình cục bộ) hoặc `git` (cấu hình từ Git repository).

- **Chức năng chính**:
  - Cung cấp cấu hình tập trung cho các service khác.
  - Hỗ trợ cập nhật cấu hình động mà không cần khởi động lại service.
  - Đảm bảo tính nhất quán của cấu hình trên toàn hệ thống.

---

### 2. Discovery Service
- **Mô tả**: 
  Đây là một **Eureka Server**, chịu trách nhiệm quản lý việc đăng ký và khám phá các service trong hệ thống. Discovery Service giúp các service có thể tìm thấy nhau mà không cần biết trước địa chỉ cụ thể của nhau.

- **Cách hoạt động**:
  - Khi một service khởi động, nó sẽ tự động đăng ký với Discovery Service.
  - Discovery Service lưu trữ thông tin về các service đã đăng ký (như tên, địa chỉ IP, cổng).
  - Các service khác có thể gửi yêu cầu đến Discovery Service để tìm kiếm thông tin về các service mà chúng cần giao tiếp.

- **Cấu hình chính**:
  - **Port**: Service này chạy trên cổng `8761`.
  - **Self-registration**: Discovery Service không tự đăng ký chính nó (`registerWithEureka: false`).
  - **Giao diện web**: Discovery Service cung cấp một giao diện web tại `http://localhost:8761` để theo dõi trạng thái của các service đã đăng ký.

- **Chức năng chính**:
  - Quản lý việc đăng ký và khám phá các service.
  - Cung cấp thông tin về trạng thái của các service (UP/DOWN).
  - Hỗ trợ cân bằng tải (load balancing) khi tích hợp với các công cụ như Ribbon hoặc Feign.

---

## Cách chạy dự án

### 1. Chạy Configuration Service
Configuration Service cần được khởi động đầu tiên để các service khác có thể lấy cấu hình từ nó.

- **Cách chạy**:
   ```bash
   cd configuration
   ./mvnw spring-boot:run