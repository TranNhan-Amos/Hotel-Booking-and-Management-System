    package sd19303no1.hotel_booking_and_management_system.Entity;

    import java.util.Date;

import jakarta.persistence.*;

    @Entity
    @Table(name = "customers")
    public class CustomersEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "customer_id")
        private Integer customerId;

        @Column(name = "password")
        private String password;

        @Column(name = "address")
        private String address;

        @Column(name = "name")
        private String name;

        @Column(name = "phone")
        private String phone;

        @Column(name = "cccd")
        private String cccd;

        @Column(name = "email", unique = true)
        private String email;

        // Getters and Setters
        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public String getCccd() {
            return cccd;
        }
        public void setCccd(String cccd) {
            this.cccd = cccd;
        }

        public void setStatus(String status) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setStatus'");
        }

        public void setCreatedDate(Date parsedDate) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setCreatedDate'");
        }
    }
