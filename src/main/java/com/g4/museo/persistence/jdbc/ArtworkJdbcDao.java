package com.g4.museo.persistence.jdbc;

import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.factory.GenericJdbcDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ArtworkJdbcDao extends GenericJdbcDao {
    public List<ArtworkDTO> getAllArtwork(){
        StringBuilder sql = new StringBuilder("SELECT * FROM artwork");
        List<ArtworkDTO> res = getJdbcTemplate().query(sql.toString(), (rs, i) -> {
            ArtworkDTO r = new ArtworkDTO();
            r.setIdartwork(rs.getInt("idartwork"));
            r.setName(rs.getString("name"));
            r.setAuthor_id(rs.getInt("author_id"));
            r.setPicture(rs.getBytes("picture"));
            r.setDate(rs.getTimestamp("date"));
            r.setCertified(rs.getBoolean("certified"));
            r.setStoredLocation(rs.getString("stored_location"));
            r.setCollectionId(rs.getInt("collection_id"));
            r.setStateId(rs.getInt("state_id"));
            return r;
        });
        LOGGER.info(res);
        return res;
    }
}
