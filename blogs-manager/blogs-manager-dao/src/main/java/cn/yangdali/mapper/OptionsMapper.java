package cn.yangdali.mapper;

import cn.yangdali.pojo.Options;

public interface OptionsMapper {

    /**
     * 根据ID删除
     * @param optionId 系统设置ID
     * @return 影响行数
     */
    int deleteById(Integer optionId);

    /**
     * 添加
     * @param options 系统设置
     * @return 影响行数
     */
    int insert(Options options);

    /**
     * 根据ID查询
     *
     * @param optionId 系统设置ID
     * @return 系统设置
     */
    Options getOptionsById(Integer optionId);

    /**
     * 更新
     *
     * @param options 系统信息
     * @return 影响行数
     */
    int update(Options options);

    /**
     * 获得记录
     *
     * @return 系统信息
     */
    Options getOptions();
}