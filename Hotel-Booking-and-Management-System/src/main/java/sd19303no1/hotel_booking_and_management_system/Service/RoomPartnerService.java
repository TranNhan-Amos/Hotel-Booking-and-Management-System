package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomPartnerRepository;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomPartnerEntity;

@Service
public class RoomPartnerService {

    @Autowired
    private RoomPartnerRepository roomPartnerRepository;
   
    public List<RoomPartnerEntity> findByPartnerId(Long  partnerId) {
        return roomPartnerRepository.findByPartnerId(partnerId);
    }

    public long countRoomsByPartnerId(Long partnerId) {
        return roomPartnerRepository.countRoomsByPartnerId(partnerId);
    }

    @Transactional
    public RoomPartnerEntity save(RoomPartnerEntity roomPartner) {
        return roomPartnerRepository.save(roomPartner);
    }
public boolean existsByRoomNumberAndPartnerId(String roomNumber, Long partnerId) {
    return roomPartnerRepository.existsByRoomNumberAndPartnerId(roomNumber, partnerId);
}

@Transactional
public void updateRoomPartner(RoomPartnerEntity room) {
    roomPartnerRepository.save(room);
}
public RoomPartnerEntity findById(Long roomId) {
    return roomPartnerRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
}

    
    
}