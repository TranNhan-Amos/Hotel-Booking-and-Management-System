/**
 * Utility functions để xử lý ảnh
 */

// Xử lý lỗi ảnh
function handleImageError(img) {
    img.onerror = null; // Tránh vòng lặp vô hạn
    img.src = '/img/rooms/default-room.jpg';
    img.classList.add('error');
    img.classList.remove('loading');
}

// Xử lý ảnh load thành công
function handleImageLoad(img) {
    img.classList.remove('loading', 'error');
    img.classList.add('loaded');
}

// Tạo ảnh với fallback
function createImageWithFallback(src, alt, className = '') {
    const img = document.createElement('img');
    img.src = src;
    img.alt = alt;
    img.className = `room-image loading ${className}`;
    
    img.onload = () => handleImageLoad(img);
    img.onerror = () => handleImageError(img);
    
    return img;
}

// Kiểm tra ảnh có tồn tại không
function checkImageExists(url) {
    return new Promise((resolve) => {
        const img = new Image();
        img.onload = () => resolve(true);
        img.onerror = () => resolve(false);
        img.src = url;
    });
}

// Preload ảnh
function preloadImage(src) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = () => reject(new Error(`Failed to load image: ${src}`));
        img.src = src;
    });
}

// Lazy load ảnh
function lazyLoadImages() {
    const images = document.querySelectorAll('img[data-src]');
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.removeAttribute('data-src');
                observer.unobserve(img);
            }
        });
    });
    
    images.forEach(img => imageObserver.observe(img));
}

// Khởi tạo khi DOM ready
document.addEventListener('DOMContentLoaded', function() {
    lazyLoadImages();
    
    // Xử lý tất cả ảnh có class room-image
    document.querySelectorAll('.room-image').forEach(img => {
        if (!img.complete) {
            img.classList.add('loading');
        }
        
        img.addEventListener('load', () => handleImageLoad(img));
        img.addEventListener('error', () => handleImageError(img));
    });
}); 