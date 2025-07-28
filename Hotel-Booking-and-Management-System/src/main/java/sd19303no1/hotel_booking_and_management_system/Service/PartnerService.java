package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.PartnerRepository;
import sd19303no1.hotel_booking_and_management_system.DTO.PartnerDTO;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;

@Service
public class PartnerService {
    
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private BookingOrderRepository bookingOrderRepository;
    @Autowired
    private RoomRepository roomRepository;
    
    public PartnerEntity findBySystemUser(SystemUserEntity systemUser) {
        return partnerRepository.findBySystemUser(systemUser);
    }
    
    public PartnerEntity findByEmail(String email) {
        return partnerRepository.findByEmail(email);
    }
    
    public PartnerEntity save(PartnerEntity partner) {
        return partnerRepository.save(partner);
    }

    public List<PartnerEntity> findAll() {
        return partnerRepository.findAll();
    }

    public PartnerEntity findById(Long id) {
        return partnerRepository.findById(id).orElse(null);
    }

    public List<PartnerDTO> getAllPartnerDTOs() {
        List<PartnerEntity> partners = partnerRepository.findAll();
        List<PartnerDTO> dtos = new java.util.ArrayList<>();
        for (PartnerEntity p : partners) {
            PartnerDTO dto = new PartnerDTO();
            dto.setPartnerId(p.getId());
            dto.setCompanyName(p.getCompanyName());
            dto.setEmail(p.getEmail());
            dto.setPhone(p.getPhone());
            dto.setAddress(p.getAddress());
            dto.setContactPerson(p.getContactPerson());
            dto.setTaxId(p.getTaxId());
            dto.setBusinessLicense(p.getBusinessLicense());
            dto.setBusinessmodel(p.getBusinessmodel());
            dto.setHotelamenities(p.getHotelamenities());
            dto.setStatus("ACTIVE"); // Mặc định
            dto.setCreatedDate(new java.util.Date()); // Mặc định
            
            // Room count
            int roomCount = roomRepository.findByPartner_Id(p.getId()).size();
            dto.setRoomCount(roomCount);
            
            // Total revenue
            double totalRevenue = bookingOrderRepository.findAllBookingsByPartner(p.getId())
                .stream().mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0).sum();
            dto.setTotalRevenue(totalRevenue);
            
            // Average rating (tạm để 0)
            dto.setAverageRating(0);
            
            dtos.add(dto);
        }
        return dtos;
    }

    public PartnerDTO getPartnerDTOById(Long partnerId) {
        PartnerEntity partner = partnerRepository.findById(partnerId).orElse(null);
        if (partner == null) return null;
        
        PartnerDTO dto = new PartnerDTO();
        dto.setPartnerId(partner.getId());
        dto.setCompanyName(partner.getCompanyName());
        dto.setEmail(partner.getEmail());
        dto.setPhone(partner.getPhone());
        dto.setAddress(partner.getAddress());
        dto.setContactPerson(partner.getContactPerson());
        dto.setTaxId(partner.getTaxId());
        dto.setBusinessLicense(partner.getBusinessLicense());
        dto.setBusinessmodel(partner.getBusinessmodel());
        dto.setHotelamenities(partner.getHotelamenities());
        dto.setStatus("ACTIVE"); // Mặc định
        dto.setCreatedDate(new java.util.Date()); // Mặc định
        
        // Room count
        int roomCount = roomRepository.findByPartner_Id(partner.getId()).size();
        dto.setRoomCount(roomCount);
        
        // Total revenue
        double totalRevenue = bookingOrderRepository.findAllBookingsByPartner(partner.getId())
            .stream().mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0).sum();
        dto.setTotalRevenue(totalRevenue);
        
        // Average rating
        dto.setAverageRating(0);
        
        return dto;
    }

    public void deletePartner(Long partnerId) {
        if (partnerRepository.existsById(partnerId)) {
            partnerRepository.deleteById(partnerId);
        } else {
            throw new IllegalArgumentException("Không tìm thấy đối tác để xóa!");
        }
    }

    public void updatePartner(PartnerEntity partner) {
        if (partner.getId() != null && partnerRepository.existsById(partner.getId())) {
            partnerRepository.save(partner);
        } else {
            throw new IllegalArgumentException("Không tìm thấy đối tác để cập nhật!");
        }
    }
}
