package com.g4.museo.persistence.jdbc;

import com.g4.museo.persistence.dto.OwnerDTO;
import com.g4.museo.persistence.factory.GenericJdbcDao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OwnerJdbcDao extends GenericJdbcDao {
    @Cacheable("owners")
    public List<OwnerDTO> getOwners(){
        StringBuilder sql = new StringBuilder("SELECT * FROM owner");
        return getJdbcTemplate().query(sql.toString(), (rs, i) -> {
            OwnerDTO o = new OwnerDTO();
            o.setOwnerID(rs.getInt("idowner"));
            o.setFirstname(rs.getString("firstname"));
            o.setLastname(rs.getString("lastname"));
            o.setOrga(rs.getString("organisation"));
            o.setAdress(rs.getString("adress"));
            return o;
        });
    }
}
