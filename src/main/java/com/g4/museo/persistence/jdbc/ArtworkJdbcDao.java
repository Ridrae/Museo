package com.g4.museo.persistence.jdbc;

import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.dto.ArtworkDetailDTO;
import com.g4.museo.persistence.dto.ArtworkFullDTO;
import com.g4.museo.persistence.factory.GenericJdbcDao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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
    public List<ArtworkFullDTO> getAllArtwork(){
        StringBuilder sql = new StringBuilder("SELECT a.idartwork, a.name, a.picture, a.author_id, a.date, a.certified, a.stored_location, a.collection_id, a.state_id, a.borrowed, a.description, ");
        sql.append("au.fullname AS author_name, ");
        sql.append("c.name AS collection_name, ");
        sql.append("s.state_name, ");
        sql.append("b.date_borrowed, b.date_return, b.is_stored, b.is_long_term, ");
        sql.append("o.idowner, o.firstname AS owner_firstname, o.lastname AS owner_lastname, o.organisation AS owner_orga, o.adress AS owner_adress, ");
        sql.append("d.width, d.height, d.perimeter, d.insurance_number, d.material, d.technic, d.type, d.is_restored ");
        sql.append("FROM  artwork AS a JOIN author AS au ON a.author_id = au.idauthor ");
        sql.append("JOIN collection AS c ON a.collection_id = c.idcollection ");
        sql.append("JOIN artwork_state AS s ON a.state_id = s.idstate ");
        sql.append("JOIN artwork_borrow AS b ON a.idartwork = b.idartwork ");
        sql.append("JOIN owner AS o ON b.idowner = o.idowner ");
        sql.append("JOIN artwork_details AS d ON a.idartwork = d.idartwork");
        List<ArtworkFullDTO> res = getJdbcTemplate().query(sql.toString(), (rs, i) -> {
            ArtworkFullDTO r = new ArtworkFullDTO();
            r.getArtworkDTO().setIdartwork(rs.getInt("idartwork"));
            r.getArtworkDTO().setName(rs.getString("name"));
            r.getArtworkDTO().setPicture(rs.getBytes("picture"));
            r.getArtworkDTO().setAuthorID(rs.getInt("author_id"));
            r.getArtworkDTO().setDate(rs.getTimestamp("date"));
            r.getArtworkDTO().setCertified(rs.getBoolean("certified"));
            r.getArtworkDTO().setStoredLocation(rs.getString("stored_location"));
            r.getArtworkDTO().setCollectionID(rs.getInt("collection_id"));
            r.getArtworkDTO().setStateID(rs.getInt("state_id"));
            r.getArtworkDTO().setBorrowed(rs.getBoolean("borrowed"));

            r.getArtworkBorrowDTO().setIdartwork(rs.getInt("idartwork"));
            r.getArtworkBorrowDTO().setIdowner(rs.getInt("idowner"));
            r.getArtworkBorrowDTO().setDateBorrowed(rs.getTimestamp("date_borrowed"));
            r.getArtworkBorrowDTO().setReturnDate(rs.getTimestamp("date_return"));
            r.getArtworkBorrowDTO().setStored(rs.getBoolean("is_stored"));
            r.getArtworkBorrowDTO().setLongTerm(rs.getBoolean("is_long_term"));

            r.getArtworkDetailDTO().setIdartwork(rs.getInt("idartwork"));
            r.getArtworkDetailDTO().setWidth(rs.getString("width"));
            r.getArtworkDetailDTO().setHeight(rs.getString("height"));
            r.getArtworkDetailDTO().setPerimeter(rs.getString("perimeter"));
            r.getArtworkDetailDTO().setInsuranceNumber(rs.getString("insurance_number"));
            r.getArtworkDetailDTO().setMaterial(rs.getString("material"));
            r.getArtworkDetailDTO().setTechnic(rs.getString("technic"));
            r.getArtworkDetailDTO().setType(rs.getString("type"));
            r.getArtworkDetailDTO().setRestored(rs.getBoolean("is_restored"));

            r.getAuthorDTO().setAuthorID(rs.getInt("author_id"));
            r.getAuthorDTO().setFullname(rs.getString("author_name"));

            r.getCollectionDTO().setCollectionName(rs.getString("collection_name"));
            r.getCollectionDTO().setCollectionID(rs.getInt("collection_id"));

            r.getOwnerDTO().setOwnerID(rs.getInt("idowner"));
            r.getOwnerDTO().setFirstname(rs.getString("owner_firstname"));
            r.getOwnerDTO().setLastname(rs.getString("owner_lastname"));
            r.getOwnerDTO().setOrga(rs.getString("owner_orga"));
            r.getOwnerDTO().setAdress(rs.getString("owner_adress"));

            r.getStateDTO().setStateID(rs.getInt("state_id"));
            r.getStateDTO().setStateName(rs.getString("state_name"));

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

    public void createArtwork(ArtworkFullDTO artwork) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate()).withTableName("artwork").usingGeneratedKeyColumns("idartwork");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", artwork.getArtworkDTO().getName());
        params.put("author_id", artwork.getArtworkDTO().getAuthorID());
        params.put("picture", artwork.getArtworkDTO().getPicture());
        params.put("date", artwork.getArtworkDTO().getDate());
        params.put("certified", artwork.getArtworkDTO().isCertified());
        params.put("stored_location", artwork.getArtworkDTO().getStoredLocation());
        params.put("collection_id", artwork.getArtworkDTO().getCollectionID());
        params.put("state_id", artwork.getArtworkDTO().getStateID());
        params.put("borrowed", artwork.getArtworkDTO().isBorrowed());
        int key = insert.executeAndReturnKey(params).intValue();

        insert = new SimpleJdbcInsert(getJdbcTemplate()).withTableName("artwork_borrow");
        params = new HashMap<String, Object>();
        params.put("idartwork", key);
        params.put("idowner", artwork.getArtworkBorrowDTO().getIdowner());
        params.put("date_borrowed", artwork.getArtworkBorrowDTO().getDateBorrowed());
        params.put("date_return", artwork.getArtworkBorrowDTO().getReturnDate());
        params.put("is_stored", artwork.getArtworkBorrowDTO().isStored());
        params.put("is_long_term", artwork.getArtworkBorrowDTO().isLongTerm());
        insert.execute(params);

        insert = new SimpleJdbcInsert(getJdbcTemplate()).withTableName("artwork_details");
        params = new HashMap<String, Object>();
        params.put("idartwork", key);
        params.put("width", artwork.getArtworkDetailDTO().getWidth());
        params.put("height", artwork.getArtworkDetailDTO().getHeight());
        params.put("perimeter", artwork.getArtworkDetailDTO().getPerimeter());
        params.put("insurance_number", artwork.getArtworkDetailDTO().getInsuranceNumber());
        params.put("material", artwork.getArtworkDetailDTO().getInsuranceNumber());
        params.put("technic", artwork.getArtworkDetailDTO().getTechnic());
        params.put("type", artwork.getArtworkDetailDTO().getType());
        params.put("is_restored", artwork.getArtworkDetailDTO().isRestored());
        insert.execute(params);
    }
}
