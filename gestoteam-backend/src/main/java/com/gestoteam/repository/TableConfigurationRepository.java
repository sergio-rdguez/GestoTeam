package com.gestoteam.repository;

import com.gestoteam.model.TableConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableConfigurationRepository extends JpaRepository<TableConfiguration, Long> {
    
    @Query("SELECT tc FROM TableConfiguration tc " +
           "WHERE tc.userId = :userId AND tc.tableName = :tableName " +
           "ORDER BY tc.updatedAt DESC")
    Optional<TableConfiguration> findByUserIdAndTableNameOrderByUpdatedAtDesc(
            @Param("userId") Long userId, 
            @Param("tableName") String tableName);
    
    @Query("SELECT tc FROM TableConfiguration tc " +
           "WHERE tc.userId = :userId " +
           "ORDER BY tc.tableName ASC")
    List<TableConfiguration> findByUserIdOrderByTableNameAsc(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(tc) > 0 FROM TableConfiguration tc " +
           "WHERE tc.userId = :userId AND tc.tableName = :tableName")
    boolean existsByUserIdAndTableName(@Param("userId") Long userId, @Param("tableName") String tableName);
    
    void deleteByUserIdAndTableName(@Param("userId") Long userId, @Param("tableName") String tableName);
}
