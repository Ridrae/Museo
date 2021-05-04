package com.g4.museo.persistence.jdbc;

import com.g4.museo.persistence.dto.CollectionDTO;
import com.g4.museo.persistence.dto.StateDTO;
import com.g4.museo.persistence.factory.GenericJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StateJdbcDao extends GenericJdbcDao {
    public List<StateDTO> getStates(){
        StringBuilder sql = new StringBuilder("SELECT * from artwork_state");
        List<StateDTO> res = getJdbcTemplate().query(sql.toString(), (rs, i) -> {
            StateDTO s = new StateDTO();
            s.setStateID(rs.getInt("idstate"));
            s.setStateName(rs.getString("state_name"));
            return s;
        });
        return res;
    }
}
