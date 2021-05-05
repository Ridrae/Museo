package com.g4.museo.persistence.jdbc;

import com.g4.museo.persistence.dto.CollectionDTO;
import com.g4.museo.persistence.factory.GenericJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Repository
public class CollectionJdbcDao extends GenericJdbcDao {
    public List<CollectionDTO> getCollections(){
        StringBuilder sql = new StringBuilder("SELECT * from collection");
        List<CollectionDTO> res = getJdbcTemplate().query(sql.toString(), (rs, i) -> {
            CollectionDTO c = new CollectionDTO();
            c.setCollectionID(rs.getInt("idcollection"));
            c.setCollectionName(rs.getString("name"));
            return c;
        });
        return res;
    }
}
