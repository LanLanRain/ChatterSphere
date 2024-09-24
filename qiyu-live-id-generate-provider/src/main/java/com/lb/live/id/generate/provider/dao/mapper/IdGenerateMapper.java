package com.lb.live.id.generate.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lb.live.id.generate.provider.dao.po.IdGeneratePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IdGenerateMapper extends BaseMapper<IdGeneratePO> {


    @Select("select * from t_id_generate_config")
    List<IdGeneratePO> selectAll();

    /**
     * 更新 ID 生成配置的下一个阈值、当前起始值和版本号
     *
     * 此方法用于更新指定 ID 的 ID 生成配置，增加下一个阈值和当前起始值，并增加版本号。
     * 它确保在乐观锁机制下，如果版本号匹配，则进行更新操作。
     *
     * @param id 要更新的配置的唯一标识符
     * @param version 当前配置的版本号，用于乐观锁控制
     * @return 返回更新操作影响的行数，通常为 1，如果版本不匹配则返回 0
     */
    @Update("update t_id_generate_config set next_threshold=next_threshold+step," +
            "current_start=current_start+step,version=version+1 where id =#{id} and version=#{version}")
    int updateNewIdCountAndVersion(@Param("id")int id,@Param("version")int version);
}