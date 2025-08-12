// Navbar JavaScript for Hotel Booking System
document.addEventListener('DOMContentLoaded', function() {
    initializeNavbar();
});

function initializeNavbar() {
    // Cache DOM elements
    const userDropdownBtn = document.getElementById('user-avatar-btn');
    const userDropdownMenu = document.getElementById('user-dropdown-menu');
    const mobileMenuBtn = document.getElementById('mobile-menu-toggle');
    const mobileMenu = document.getElementById('mobile-menu');
    const mobileMenuClose = document.querySelector('.mobile-menu-close');
    const notificationBtn = document.querySelector('.notification-btn');

    // User dropdown functionality
    if (userDropdownBtn && userDropdownMenu) {
        userDropdownBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleUserDropdown();
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!userDropdownBtn.contains(e.target) && !userDropdownMenu.contains(e.target)) {
                closeUserDropdown();
            }
        });

        // Keyboard navigation for dropdown
        userDropdownBtn.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                toggleUserDropdown();
            }
        });
    }

    // Mobile menu functionality
    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleMobileMenu();
        });

        if (mobileMenuClose) {
            mobileMenuClose.addEventListener('click', function() {
                closeMobileMenu();
            });
        }

        // Close mobile menu when clicking outside
        mobileMenu.addEventListener('click', function(e) {
            if (e.target === mobileMenu) {
                closeMobileMenu();
            }
        });

        // Close mobile menu when clicking on links
        const mobileMenuLinks = mobileMenu.querySelectorAll('.mobile-menu-link');
        mobileMenuLinks.forEach(link => {
            link.addEventListener('click', function() {
                closeMobileMenu();
            });
        });
    }

    // Notification functionality
    if (notificationBtn) {
        notificationBtn.addEventListener('click', function() {
            handleNotificationClick();
        });
    }

    // Set active page
    setActivePage();
}

function toggleUserDropdown() {
    const userDropdownBtn = document.getElementById('user-avatar-btn');
    const userDropdownMenu = document.getElementById('user-dropdown-menu');
    
    if (userDropdownMenu.classList.contains('show')) {
        closeUserDropdown();
    } else {
        openUserDropdown();
    }
}

function openUserDropdown() {
    const userDropdownBtn = document.getElementById('user-avatar-btn');
    const userDropdownMenu = document.getElementById('user-dropdown-menu');
    
    userDropdownMenu.classList.add('show');
    userDropdownBtn.setAttribute('aria-expanded', 'true');
    userDropdownMenu.setAttribute('aria-hidden', 'false');
    
    // Focus first dropdown item
    const firstItem = userDropdownMenu.querySelector('.dropdown-item');
    if (firstItem) {
        firstItem.focus();
    }
}

function closeUserDropdown() {
    const userDropdownBtn = document.getElementById('user-avatar-btn');
    const userDropdownMenu = document.getElementById('user-dropdown-menu');
    
    userDropdownMenu.classList.remove('show');
    userDropdownBtn.setAttribute('aria-expanded', 'false');
    userDropdownMenu.setAttribute('aria-hidden', 'true');
}

function toggleMobileMenu() {
    const mobileMenu = document.getElementById('mobile-menu');
    const body = document.body;
    
    if (mobileMenu.classList.contains('show')) {
        closeMobileMenu();
    } else {
        openMobileMenu();
    }
}

function openMobileMenu() {
    const mobileMenu = document.getElementById('mobile-menu');
    const mobileMenuBtn = document.getElementById('mobile-menu-toggle');
    const body = document.body;
    
    mobileMenu.classList.add('show');
    body.classList.add('mobile-menu-open');
    mobileMenuBtn.setAttribute('aria-expanded', 'true');
    mobileMenu.setAttribute('aria-hidden', 'false');
}

function closeMobileMenu() {
    const mobileMenu = document.getElementById('mobile-menu');
    const mobileMenuBtn = document.getElementById('mobile-menu-toggle');
    const body = document.body;
    
    mobileMenu.classList.remove('show');
    body.classList.remove('mobile-menu-open');
    mobileMenuBtn.setAttribute('aria-expanded', 'false');
    mobileMenu.setAttribute('aria-hidden', 'true');
}

function handleNotificationClick() {
    // Redirect to notifications page
    window.location.href = '/Notification';
}

function setActivePage() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-links a, .mobile-menu-link');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath || (currentPath === '/' && href === '/')) {
            link.classList.add('active');
            link.setAttribute('aria-current', 'page');
        } else {
            link.classList.remove('active');
            link.removeAttribute('aria-current');
        }
    });
}

// Global functions for inline onclick handlers
window.toggleUserDropdown = toggleUserDropdown;
window.toggleMobileMenu = toggleMobileMenu;
window.handleNotificationClick = handleNotificationClick;
