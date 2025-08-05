# Performance Optimization - N+1 Query Problem Fix

## Problem Description

The application had potential N+1 query problems when loading `BookingOrderEntity` objects with their related entities (`Room`, `Partner`, `Customer`, `Status`, `Voucher`). This occurred because:

1. Many service methods used the basic `findById()` repository method
2. Related entities were marked with `@JsonIgnore` and `FetchType.LAZY`
3. When these relationships were accessed later in the code, each one triggered a separate database query

## Example of the Problem

**Before Optimization:**
```java
// This triggers 1 query to get the booking
BookingOrderEntity booking = bookingOrderRepository.findById(bookingId);

// Each of these may trigger additional queries (N+1 problem)
booking.getRoom().getPartner();  // +1 query for room, +1 for partner
booking.getCustomer();           // +1 query for customer  
booking.getStatus();             // +1 query for status
booking.getVoucher();            // +1 query for voucher
```

**Result:** 1 + N queries where N is the number of related entities accessed.

## Solution Implemented

### 1. Created Optimized Repository Method

Added `findByIdWithDetails()` method in `BookingOrderRepository`:

```java
@Query("SELECT b FROM BookingOrderEntity b " +
       "LEFT JOIN FETCH b.customer " +
       "LEFT JOIN FETCH b.room r " +
       "LEFT JOIN FETCH r.partner " +
       "LEFT JOIN FETCH b.status " +
       "LEFT JOIN FETCH b.voucher " +
       "WHERE b.bookingId = :bookingId")
Optional<BookingOrderEntity> findByIdWithDetails(@Param("bookingId") Integer bookingId);
```

### 2. Updated Critical Service Methods

Updated the following methods to use the optimized query:

- `confirmPayment(Integer bookingId)`
- `confirmPaymentAtHotel(Integer bookingId)`  
- `cancelBooking(Integer bookingId, String reason)`

### 3. Performance Benefits

**After Optimization:**
```java
// This triggers 1 query that fetches ALL related entities using JOINs
BookingOrderEntity booking = bookingOrderRepository.findByIdWithDetails(bookingId);

// These no longer trigger additional queries - data is already loaded
booking.getRoom().getPartner();  // No additional query
booking.getCustomer();           // No additional query
booking.getStatus();             // No additional query  
booking.getVoucher();            // No additional query
```

**Result:** Always exactly 1 query regardless of how many relationships are accessed.

## Performance Impact

- **Database queries reduced** from 1+N to 1 for booking operations
- **Response time improved** especially for bookings with many relationships
- **Database load reduced** by eliminating redundant queries
- **Memory usage optimized** by loading related data in a single efficient query

## Recommendations for Future Development

1. **Always consider JOIN FETCH** when designing repository methods that will access related entities
2. **Profile queries** in development to identify N+1 problems early
3. **Use JPQL with JOIN FETCH** instead of basic repository methods when relationships will be accessed
4. **Monitor database query logs** to identify performance bottlenecks
5. **Consider using @EntityGraph** annotation for more complex scenarios

## Additional Areas for Optimization

The following areas could benefit from similar optimizations:

1. **Bulk operations** on multiple bookings (findAllByEmailOrderByCreatedAtDesc)
2. **Report generation** queries that access multiple relationships
3. **Dashboard data loading** that displays booking summaries
4. **Search and filtering** operations that need related entity data

## Migration Notes

- The optimization is backward compatible
- Existing code using `findById()` will continue to work
- New optimized methods should be used for performance-critical operations
- No database schema changes required