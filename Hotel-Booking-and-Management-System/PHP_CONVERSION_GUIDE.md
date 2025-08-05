# 🔄 Hướng Dẫn Chuyển Đổi Sang PHP

## ⚠️ Lưu Ý Quan Trọng

**Chuyển đổi từ Spring Boot sang PHP sẽ mất nhiều thời gian và công sức.** 
Khuyến nghị sử dụng VPS thay vì chuyển đổi.

## 📋 Yêu Cầu

### **Shared Hosting Requirements:**
- **PHP:** 8.0+
- **MySQL:** 5.7+
- **Apache/Nginx:** Hỗ trợ .htaccess
- **SSL:** Let's Encrypt support

## 🔄 Quá Trình Chuyển Đổi

### **1. Database Migration**
```sql
-- Giữ nguyên cấu trúc database hiện tại
-- Chỉ cần export từ MySQL và import vào hosting
```

### **2. Entity Classes → PHP Models**
```php
<?php
// Thay vì Java Entity
class RoomEntity {
    private $id;
    private $name;
    // ...
}

// Chuyển thành PHP Model
class Room {
    private $db;
    
    public function __construct($db) {
        $this->db = $db;
    }
    
    public function findAll() {
        $stmt = $this->db->prepare("SELECT * FROM rooms");
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
    
    public function findById($id) {
        $stmt = $this->db->prepare("SELECT * FROM rooms WHERE id = ?");
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }
}
?>
```

### **3. Repository → PHP DAO**
```php
<?php
// Database Access Object
class RoomDAO {
    private $db;
    
    public function __construct($db) {
        $this->db = $db;
    }
    
    public function save($room) {
        if (isset($room['id'])) {
            return $this->update($room);
        } else {
            return $this->create($room);
        }
    }
    
    private function create($room) {
        $sql = "INSERT INTO rooms (name, price, description) VALUES (?, ?, ?)";
        $stmt = $this->db->prepare($sql);
        return $stmt->execute([$room['name'], $room['price'], $room['description']]);
    }
}
?>
```

### **4. Service Layer → PHP Services**
```php
<?php
class RoomService {
    private $roomDAO;
    
    public function __construct($roomDAO) {
        $this->roomDAO = $roomDAO;
    }
    
    public function getAllRooms() {
        return $this->roomDAO->findAll();
    }
    
    public function getRoomById($id) {
        return $this->roomDAO->findById($id);
    }
    
    public function createRoom($roomData) {
        // Validation
        if (empty($roomData['name'])) {
            throw new Exception('Room name is required');
        }
        
        return $this->roomDAO->save($roomData);
    }
}
?>
```

### **5. Controller → PHP Controllers**
```php
<?php
class RoomController {
    private $roomService;
    
    public function __construct($roomService) {
        $this->roomService = $roomService;
    }
    
    public function index() {
        try {
            $rooms = $this->roomService->getAllRooms();
            include 'views/rooms/index.php';
        } catch (Exception $e) {
            include 'views/error.php';
        }
    }
    
    public function show($id) {
        try {
            $room = $this->roomService->getRoomById($id);
            include 'views/rooms/show.php';
        } catch (Exception $e) {
            include 'views/error.php';
        }
    }
}
?>
```

### **6. Thymeleaf → PHP Templates**
```php
<!-- Thay vì Thymeleaf -->
<!-- <div th:each="room : ${rooms}"> -->

<!-- Chuyển thành PHP -->
<?php foreach ($rooms as $room): ?>
    <div class="room-card">
        <h3><?= htmlspecialchars($room['name']) ?></h3>
        <p><?= htmlspecialchars($room['description']) ?></p>
        <span class="price">$<?= number_format($room['price']) ?></span>
    </div>
<?php endforeach; ?>
```

## 📁 Cấu Trúc Thư Mục PHP

```
public_html/
├── index.php
├── config/
│   ├── database.php
│   └── config.php
├── models/
│   ├── Room.php
│   ├── Booking.php
│   └── User.php
├── controllers/
│   ├── RoomController.php
│   ├── BookingController.php
│   └── AuthController.php
├── views/
│   ├── layouts/
│   │   └── main.php
│   ├── rooms/
│   │   ├── index.php
│   │   └── show.php
│   └── auth/
│       ├── login.php
│       └── register.php
├── assets/
│   ├── css/
│   ├── js/
│   └── img/
└── .htaccess
```

## 🔧 Cấu Hình Database

### **config/database.php**
```php
<?php
class Database {
    private $host = 'localhost';
    private $db_name = 'nhanvmco_DATN';
    private $username = 'nhanvmco_nhaan';
    private $password = 'wNWqvYMxs4cEVGDwz78q';
    private $conn;
    
    public function getConnection() {
        $this->conn = null;
        
        try {
            $this->conn = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name,
                $this->username,
                $this->password
            );
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch(PDOException $exception) {
            echo "Connection error: " . $exception->getMessage();
        }
        
        return $this->conn;
    }
}
?>
```

## 🚀 Deployment Steps

### **1. Upload Files**
- Upload tất cả file PHP lên `public_html`
- Đảm bảo permissions đúng (644 cho files, 755 cho folders)

### **2. Cấu Hình .htaccess**
```apache
RewriteEngine On
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule ^(.*)$ index.php?url=$1 [QSA,L]

# Security headers
Header always set X-Content-Type-Options nosniff
Header always set X-Frame-Options DENY
Header always set X-XSS-Protection "1; mode=block"
```

### **3. Test Application**
- Truy cập domain để kiểm tra
- Kiểm tra database connection
- Test các chức năng chính

## ⏱️ Thời Gian Ước Tính

- **Database migration:** 1-2 ngày
- **Core models & DAO:** 3-5 ngày
- **Controllers & Views:** 5-7 ngày
- **Testing & Debug:** 2-3 ngày
- **Total:** 2-3 tuần

## 💡 Khuyến Nghị

**Thay vì chuyển đổi sang PHP, hãy cân nhắc:**

1. **Sử dụng VPS** ($5-10/tháng)
2. **Cloud platforms** (Heroku free tier)
3. **Docker deployment**
4. **Microservices architecture**

## 📞 Hỗ Trợ

Nếu quyết định chuyển đổi, hãy:
1. Backup toàn bộ code hiện tại
2. Tạo branch mới cho PHP version
3. Test từng module một
4. Maintain cả hai version song song 