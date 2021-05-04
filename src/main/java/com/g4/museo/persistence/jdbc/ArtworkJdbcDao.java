package com.g4.museo.persistence.jdbc;

import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.dto.ArtworkDetailDTO;
import com.g4.museo.persistence.factory.GenericJdbcDao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ArtworkJdbcDao extends GenericJdbcDao {
    @Cacheable("artworks")
    public List<ArtworkDTO> getAllArtwork(){
        StringBuilder sql = new StringBuilder("SELECT a.idartwork, a.name, a.picture, a.date, a.certified, a.stored_location, au.fullname AS author_name, c.name AS collection_name, s.state_name, a.borrowed ");
        sql.append("FROM  artwork AS a JOIN author AS au ON a.author_id = au.idauthor ");
        sql.append("JOIN collection AS c ON a.collection_id = c.idcollection ");
        sql.append("JOIN artwork_state AS s ON a.state_id = s.idstate");
        List<ArtworkDTO> res = getJdbcTemplate().query(sql.toString(), (rs, i) -> {
            ArtworkDTO r = new ArtworkDTO();
            r.setIdartwork(rs.getInt("idartwork"));
            r.setName(rs.getString("name"));
            r.setAuthorName(rs.getString("author_name"));
            r.setPicture(rs.getBytes("picture"));
            r.setDate(rs.getTimestamp("date"));
            r.setCertified(rs.getBoolean("certified"));
            r.setStoredLocation(rs.getString("stored_location"));
            r.setCollectionName(rs.getString("collection_name"));
            r.setState(rs.getString("state_name"));
            r.setBorrowed(rs.getBoolean("borrowed"));
            return r;
        });
        return res;
    }

    public ArtworkDetailDTO getArtworkDetailByID(int id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder sql = new StringBuilder("SELECT * FROM artwork_details WHERE idartwork = :id");
        ArtworkDetailDTO res = getNamedParameterJdbcTemplate().query(sql.toString(), params, (rs) -> {
            ArtworkDetailDTO r = new ArtworkDetailDTO();
            r.setIdartwork(rs.getInt("idartwork"));
            r.setWidth(rs.getString("width"));
            r.setHeight(rs.getString("height"));
            r.setPerimeter(rs.getString("perimeter"));
            r.setInsuranceNumber(rs.getString("insurance_number"));
            r.setMaterial(rs.getString("material"));
            r.setTechnic(rs.getString("technic"));
            r.setType(rs.getString("type"));
            r.setRestored(rs.getBoolean("is_restored"));
            return r;
        });
        return res;
    }

    @Cacheable("returnDate")
    public Timestamp getReturnDateByID(int id){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder sql = new StringBuilder("SELECT date_return from artwork_borrow WHERE idartwork = :id");
        RowMapper<Timestamp> rm = new RowMapper() {
            @Override
            public Timestamp mapRow(ResultSet resultSet, int i) throws SQLException {
                Timestamp date = resultSet.getTimestamp("date_return");
                return date;
            }
        };
        return getNamedParameterJdbcTemplate().query(sql.toString(), params, rm).get(0);
    }
}
