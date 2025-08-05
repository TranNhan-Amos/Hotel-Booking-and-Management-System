# 🚀 Hướng Dẫn Deploy Spring Boot Application

## 📋 Yêu Cầu Hệ Thống

### **Option 1: VPS/Dedicated Server (Khuyến nghị)**
- **OS:** Ubuntu 20.04+ / CentOS 8+
- **RAM:** Tối thiểu 2GB (khuyến nghị 4GB+)
- **Storage:** 20GB+
- **Java:** OpenJDK 21
- **Database:** MySQL 8.0+

### **Option 2: Cloud Platforms**
- **Heroku** (Free tier có sẵn)
- **AWS Elastic Beanstalk**
- **Google Cloud Platform**
- **DigitalOcean App Platform**

## 🔧 Cài Đặt Trên VPS

### **1. Cài đặt Java 21**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# CentOS/RHEL
sudo yum install java-21-openjdk-devel
```

### **2. Cài đặt Maven**
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven
```

### **3. Cài đặt MySQL**
```bash
# Ubuntu/Debian
sudo apt install mysql-server

# CentOS/RHEL
sudo yum install mysql-server
```

### **4. Build và Deploy**
```bash
# Clone project
git clone <your-repo-url>
cd Hotel-Booking-and-Management-System

# Build project
mvn clean package

# Chạy application
java -jar target/hotel-booking-and-management-system-0.0.1-SNAPSHOT.war
```

## 🌐 Cấu Hình Domain

### **1. DNS Configuration**
- Point domain A record đến IP của VPS
- Cấu hình subdomain nếu cần

### **2. Nginx Reverse Proxy (Tùy chọn)**
```nginx
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 🔒 Bảo Mật

### **1. Firewall**
```bash
# Mở port cần thiết
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw allow 3306  # MySQL (nếu remote)
```

### **2. SSL Certificate**
```bash
# Cài đặt Certbot
sudo apt install certbot python3-certbot-nginx

# Tạo SSL certificate
sudo certbot --nginx -d yourdomain.com
```

## 📊 Monitoring

### **1. Systemd Service**
```bash
# Tạo service file
sudo nano /etc/systemd/system/hotel-booking.service

[Unit]
Description=Hotel Booking Application
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/hotel-booking
ExecStart=/usr/bin/java -jar hotel-booking-and-management-system-0.0.1-SNAPSHOT.war
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

### **2. Enable và Start Service**
```bash
sudo systemctl enable hotel-booking
sudo systemctl start hotel-booking
sudo systemctl status hotel-booking
```

## 🚨 Troubleshooting

### **1. Kiểm tra logs**
```bash
# Application logs
sudo journalctl -u hotel-booking -f

# System logs
sudo tail -f /var/log/syslog
```

### **2. Kiểm tra port**
```bash
# Kiểm tra port đang listen
sudo netstat -tlnp | grep :8080

# Kiểm tra firewall
sudo ufw status
```

## 💰 Chi Phí Ước Tính

### **VPS Options:**
- **DigitalOcean:** $5-10/tháng
- **Linode:** $5-10/tháng  
- **Vultr:** $5-10/tháng
- **AWS EC2:** $10-20/tháng

### **Domain:** $10-15/năm
### **SSL:** Miễn phí (Let's Encrypt)

## 📞 Hỗ Trợ

Nếu gặp vấn đề, hãy kiểm tra:
1. Java version: `java -version`
2. Maven version: `mvn -version`
3. Database connection
4. Application logs
5. Firewall settings 