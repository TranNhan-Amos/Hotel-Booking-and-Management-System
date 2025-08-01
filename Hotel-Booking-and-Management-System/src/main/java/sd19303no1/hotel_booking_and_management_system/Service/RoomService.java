package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;

@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomRepository roomRepository;

    public List<RoomEntity> findByPartnerId(Long partnerId) {
        logger.info("Finding rooms for partnerId: {}", partnerId);
        List<RoomEntity> rooms = roomRepository.findByPartnerId(partnerId);
        logger.info("Found {} rooms for partnerId: {}", rooms.size(), partnerId);
        return rooms;
    }

    public Page<RoomEntity> findRoomsWithFilters(Long partnerId, RoomEntity.RoomStatus status, 
            RoomEntity.RoomType type, String search, Pageable pageable) {
        logger.info("Finding rooms with filters - partnerId: {}, status: {}, type: {}, search: {}", 
                partnerId, status, type, search);
        Page<RoomEntity> rooms = roomRepository.findRoomsWithFilters(partnerId, status, type, search, pageable);
        logger.info("Found {} rooms with filters", rooms.getTotalElements());
        return rooms;
    }

    public Page<RoomEntity> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Pageable pageable) {
        logger.info("Finding available rooms from {} to {}", checkInDate, checkOutDate);
        Page<RoomEntity> rooms = roomRepository.findAvailableRooms(checkInDate, checkOutDate, pageable);
        logger.info("Found {} available rooms", rooms.getTotalElements());
        return rooms;
    }

    public Page<RoomEntity> findAvailableRoomsByLocation(String location, LocalDate checkInDate, 
            LocalDate checkOutDate, Pageable pageable) {
        logger.info("Finding available rooms in location: {} from {} to {}", location, checkInDate, checkOutDate);
        Page<RoomEntity> rooms = roomRepository.findAvailableRoomsByLocation(location, checkInDate, checkOutDate, pageable);
        logger.info("Found {} available rooms in location: {}", rooms.getTotalElements(), location);
        return rooms;
    }

    public Page<RoomEntity> findRelatedRooms(RoomEntity.RoomType roomType, Integer excludeRoomId, Pageable pageable) {
        logger.info("Finding related rooms for type: {}, excluding roomId: {}", roomType, excludeRoomId);
        Page<RoomEntity> rooms = roomRepository.findRelatedRooms(roomType, excludeRoomId, pageable);
        logger.info("Found {} related rooms", rooms.getTotalElements());
        return rooms;
    }

    public Page<Object[]> findFeaturedRoomsWithRating(Pageable pageable) {
        logger.info("Finding featured rooms with ratings");
        Page<Object[]> rooms = roomRepository.findFeaturedRoomsWithRating(pageable);
        logger.info("Found {} featured rooms", rooms.getTotalElements());
        return rooms;
    }

    public List<RoomEntity.RoomType> findDistinctTypes() {
        logger.info("Finding distinct room types");
        List<RoomEntity.RoomType> types = roomRepository.findDistinctTypes();
        logger.info("Found {} distinct room types", types.size());
        return types;
    }

    public RoomEntity findFirstByType(RoomEntity.RoomType type) {
        logger.info("Finding first room by type: {}", type);
        RoomEntity room = roomRepository.findFirstByType(type);
        logger.info("Found room: {}", room != null ? room.getRoomId() : "none");
        return room;
    }

    public List<String> findAmenitiesByRoomId(Integer roomId) {
        logger.info("Finding amenities for roomId: {}", roomId);
        List<String> amenities = roomRepository.findAmenitiesByRoomId(roomId);
        logger.info("Found {} amenities for roomId: {}", amenities.size(), roomId);
        return amenities;
    }

    public Page<RoomEntity> findFeaturedRooms(Pageable pageable) {
        logger.info("Finding featured rooms");
        Page<RoomEntity> rooms = roomRepository.findFeaturedRooms(pageable);
        logger.info("Found {} featured rooms", rooms.getTotalElements());
        return rooms;
    }

    public Optional<RoomEntity> findById(Integer roomId) {
        logger.info("Finding room by id: {}", roomId);
        Optional<RoomEntity> room = roomRepository.findById(roomId);
        logger.info("Room found: {}", room.isPresent() ? room.get().getRoomId() : "none");
        return room;
    }

    public RoomEntity save(RoomEntity room) {
        logger.info("Saving room: {}", room.getRoomId());
        RoomEntity savedRoom = roomRepository.save(room);
        logger.info("Room saved successfully: {}", savedRoom.getRoomId());
        return savedRoom;
    }

    public void delete(Integer roomId) {
        logger.info("Deleting room: {}", roomId);
        roomRepository.deleteById(roomId);
        logger.info("Room deleted successfully: {}", roomId);
    }

    // Tính số phòng có sẵn trong khoảng thời gian
    public int getAvailableRoomCount(Integer roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        logger.info("Getting available room count for roomId: {} from {} to {}", roomId, checkInDate, checkOutDate);
        
        // Lấy tổng số phòng của loại phòng này
        Optional<RoomEntity> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            logger.warn("Room not found for roomId: {}", roomId);
            return 0;
        }
        
        RoomEntity room = roomOpt.get();
        int totalRooms = room.getTotalRooms() != null ? room.getTotalRooms() : 1;
        
        // Đếm số booking đã có trong khoảng thời gian này
        long bookedRooms = roomRepository.countBookedRoomsInDateRange(roomId, checkInDate, checkOutDate);
        
        int availableRooms = (int) (totalRooms - bookedRooms);
        logger.info("Available rooms for roomId {}: {} (total: {}, booked: {})", 
                roomId, availableRooms, totalRooms, bookedRooms);
        
        return Math.max(0, availableRooms);
    }

    // Tìm kiếm phòng theo loại và khoảng thời gian
    public List<RoomEntity> searchRooms(String roomType, LocalDate checkInDate, LocalDate checkOutDate, Integer roomCount) {
        logger.info("Searching rooms for type: {}, checkIn: {}, checkOut: {}, count: {}", 
                roomType, checkInDate, checkOutDate, roomCount);
        
        try {
            // Chuyển đổi roomType string thành enum
            RoomEntity.RoomType targetType = null;
            if (roomType != null && !roomType.trim().isEmpty()) {
                try {
                    targetType = RoomEntity.RoomType.valueOf(roomType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid room type: {}", roomType);
                }
            }
            
            final RoomEntity.RoomType finalType = targetType;
            
            // Sử dụng repository để tìm phòng có sẵn
            Page<RoomEntity> availableRooms = roomRepository.findAvailableRooms(checkInDate, checkOutDate, PageRequest.of(0, 50));
            
            List<RoomEntity> filteredRooms = availableRooms.getContent().stream()
                    .filter(room -> finalType == null || room.getType() == finalType)
                    .filter(room -> room.getTotalRooms() != null && room.getTotalRooms() >= roomCount)
                    .collect(Collectors.toList());
            
            logger.info("Found {} available rooms matching criteria", filteredRooms.size());
            return filteredRooms;
            
        } catch (Exception e) {
            logger.error("Error searching rooms: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Thêm các method còn thiếu
    public RoomEntity getRoomById(Integer roomId) {
        logger.info("Getting room by id: {}", roomId);
        Optional<RoomEntity> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isPresent()) {
            RoomEntity room = roomOpt.get();
            logger.info("Room found: {}", room.getRoomId());
            return room;
        } else {
            logger.warn("Room not found for id: {}", roomId);
            return null;
        }
    }

    public RoomEntity saveRoom(RoomEntity room) {
        logger.info("Saving room: {}", room.getRoomId());
        RoomEntity savedRoom = roomRepository.save(room);
        logger.info("Room saved successfully: {}", savedRoom.getRoomId());
        return savedRoom;
    }

    public void deleteRoom(Integer roomId) {
        logger.info("Deleting room: {}", roomId);
        if (roomRepository.existsById(roomId)) {
            roomRepository.deleteById(roomId);
            logger.info("Room deleted successfully: {}", roomId);
        } else {
            logger.warn("Room not found for deletion: {}", roomId);
            throw new IllegalArgumentException("Không tìm thấy phòng để xóa");
        }
    }
}