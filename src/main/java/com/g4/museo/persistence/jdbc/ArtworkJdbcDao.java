package com.g4.museo.persistence.jdbc;

import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.dto.ArtworkDetailDTO;
import com.g4.museo.persistence.factory.GenericJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ArtworkJdbcDao extends GenericJdbcDao {
    public List<ArtworkDTO> getAllArtwork(){
        StringBuilder sql = new StringBuilder("SELECT a.idartwork, a.name, a.picture, a.date, a.certified, a.stored_location, au.fullname AS artist_name, c.name AS collection_name, s.state_name ");
        sql.append("FROM  artwork AS a JOIN author AS au ON a.author_id = au.idauthor ");
        sql.append("JOIN collection AS c ON a.collection_id = c.idcollection ");
        sql.append("JOIN artwork_state AS s ON a.state_id = s.idstate");
        List<ArtworkDTO> res = getJdbcTemplate().query(sql.toString(), (rs, i) -> {
            ArtworkDTO r = new ArtworkDTO();
            r.setIdartwork(rs.getInt("idartwork"));
            r.setName(rs.getString("name"));
            r.setAuthor_id(rs.getString("author_name"));
            r.setPicture(rs.getBytes("picture"));
            r.setDate(rs.getTimestamp("date"));
            r.setCertified(rs.getBoolean("certified"));
            r.setStoredLocation(rs.getString("stored_location"));
            r.setCollectionId(rs.getString("collection_name"));
            r.setStateId(rs.getString("state"));
            return r;
        });
        return res;
    }

    public ArtworkDetailDTO getArtworkDetailByID(int id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder sql = new StringBuilder("SELECT * FROM artwork_details WHERE idartwork = :id");
        List<ArtworkDetailDTO> res = getNamedParameterJdbcTemplate().query(sql.toString(), params, (rs, i) -> {
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
        return res.get(0);
    }
}
