package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.ReviewRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.DTO.RoomTypeDTO;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    public List<RoomEntity> getFeaturedRooms(int limit) {
        List<RoomEntity> rooms = roomRepository.findAll();
        
        // Tính rating trung bình cho mỗi phòng
        rooms.forEach(room -> {
            Double avgRating = reviewRepository.findAverageRatingByRoomId(room.getRoomId());
            room.setAverageRating(avgRating != null ? avgRating : 0.0);
        });

        // Sắp xếp theo rating và lấy top rooms
        return rooms.stream()
                .sorted((r1, r2) -> Double.compare(r2.getAverageRating(), r1.getAverageRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<RoomTypeDTO> getRoomTypes() {
        List<String> distinctTypes = roomRepository.findDistinctTypes();
        return distinctTypes.stream()
                .map(type -> {
                    RoomEntity sampleRoom = roomRepository.findFirstByType(type);
                    return new RoomTypeDTO(
                        type,
                        sampleRoom.getDescription(),
                        sampleRoom.getImageUrls() != null && !sampleRoom.getImageUrls().isEmpty() 
                            ? sampleRoom.getImageUrls().get(0) 
                            : "/placeholder.svg?height=120&width=180"
                    );
                })
                .collect(Collectors.toList());
    }

    public List<RoomEntity> searchRooms(String location, LocalDate checkInDate, LocalDate checkOutDate, Integer guests) {
        if (location == null || location.trim().isEmpty()) {
            return roomRepository.findAvailableRooms(checkInDate, checkOutDate, PageRequest.of(0, 20));
        }
        return roomRepository.findAvailableRoomsByLocation(location, checkInDate, checkOutDate, PageRequest.of(0, 20));
    }

    public List<RoomEntity> getRelatedRooms(String roomType, Integer excludeRoomId, int limit) {
        return roomRepository.findRelatedRooms(roomType, excludeRoomId, PageRequest.of(0, limit));
    }

    public RoomEntity findById(Integer roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public List<RoomEntity> findAll() {
        return roomRepository.findAll();
    }


        public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

        public RoomEntity getRoomById(Integer id) {
        Optional<RoomEntity> room = roomRepository.findById(id);
        return room.orElseThrow(() -> new RuntimeException("Room not found"));
    }

        public void saveRoom(RoomEntity room) {
        roomRepository.save(room);
    }

        public void deleteRoom(Integer id) {
        roomRepository.deleteById(id);
    }

    /**
     * Tính số phòng còn trống cho một loại phòng trong khoảng ngày
     */
    public int getAvailableRoomCount(Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        RoomEntity room = roomRepository.findById(roomId).orElse(null);
        if (room == null) return 0;
        int total = room.getTotalRooms() != null ? room.getTotalRooms() : 0;
        // Đếm số booking còn hiệu lực (không bị hủy, hoàn tiền, v.v.)
        int booked = bookingOrderRepository.findConflictingBookings(roomId, checkIn, checkOut).size();
        return Math.max(total - booked, 0);
    }

    public List<RoomEntity> findByPartnerId(Long partnerId) {
        return roomRepository.findByPartner_Id(partnerId);
    }
}