package cn.com.dyg.work.base;



import cn.com.dyg.work.mybatis.DynaSqlProvider;
import cn.com.dyg.work.mybatis.QueryListParam;
import cn.com.dyg.work.mybatis.UpdateParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository(value = "baseDao")
public interface BaseDao<T extends BaseEntity> {

    /**
     * 标题: 实体类保存
     * 描述: 保存操作，传入实体类
     * <b> 默认实体类中主键为id且为自增
     * <b> 实体类中属性注解@DBField(isIgnore=true)会忽略改字段不插入数据库
     * <b> 返回自增主键值
     * 作者 : wzy
     * 日期 : 2017-9-12
     */
    @InsertProvider(type = DynaSqlProvider.class, method = "insertEntity")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int saveEntity(T t);

    @InsertProvider(type = DynaSqlProvider.class, method = "insertEntity")
    public int insertEntity(T t);

    /**
     * 标题: 批量保存
     * 描述: 批量保存操作，传入实体类集合
     * <b> 默认实体类中主键为id且为自增
     * <b> 实体类中属性注解@DBField(isIgnore=true)会忽略改字段不插入数据库
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-15
     */
    @InsertProvider(type = DynaSqlProvider.class, method = "insertEntity_batch")
    public int saveBatchEntity(List<T> t);

    /**
     * 标题: 批量更新
     * 描述: 批量保存操作，传入实体类集合
     * <b> 默认实体类中主键为id且为自增
     * <b> 实体类中属性注解@DBField(isIgnore=true)会忽略改字段不插入数据库
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-15
     */
    @UpdateProvider(type = DynaSqlProvider.class, method = "updateEntity_batch")
    public int updateEntity_batch(List<T> t);



    /**
     * 标题: 修改实体类
     * 描述: 更新操作，传入实体类
     * <b>  实体类中属性注解@DBField(isIgnore=true)会忽略改字段不更新数据库
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-12
     */
    @UpdateProvider(type = DynaSqlProvider.class, method = "editEntity")
    public int editEntity(T t);

    /**
     * 标题: 更新实体类
     * 描述: 更新操作，传入实体类
     * <b>  实体类中属性注解@DBField(isIgnore=true)以及值为空的字段会忽略不更新数据库
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-12
     */
    @UpdateProvider(type = DynaSqlProvider.class, method = "updateEntity")
    public int updateEntity(T t);



    /**
     * 标题: 更新数据
     * 描述: 根据传入数据更新特定数据
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-15
     */
    @UpdateProvider(type = DynaSqlProvider.class, method = "updateByWhere")
    public int updateByWhere(@Param("param") UpdateParam param, @Param("cls") Class<T> cls);

    /**
     * 标题: 分页查询
     * 描述: 分页查询，传入参数   pageindex：分页索引，pagenum：每页显示数
     * <b>  ordercol:排序字段，whereparam：where参数
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-12
     */
    @SelectProvider(type = DynaSqlProvider.class, method = "queryListPage_MSSQL")
    public List<T> getPageList_MSSQL(@Param("param") QueryListParam param, @Param("cls") Class<T> cls);

    @SelectProvider(type = DynaSqlProvider.class, method = "queryListPage_MYSQL")
    public List<T> getPageList_MYSQL(@Param("param") QueryListParam param, @Param("cls") Class<T> cls);

    @SelectProvider(type = DynaSqlProvider.class, method = "queryList")
    public List<T> selectList(@Param("param") QueryListParam param, @Param("cls") Class<T> cls);

    @SelectProvider(type = DynaSqlProvider.class, method = "queryList")
    public T selectOne(@Param("param") QueryListParam param, @Param("cls") Class<T> cls);

    @SelectProvider(type = DynaSqlProvider.class, method = "queryListPage_ORACLE")
    public List<T> getPageList_ORACLE(@Param("param") QueryListParam param, @Param("cls") Class<T> cls);

    @SelectProvider(type = DynaSqlProvider.class, method = "queryListByWhere")
    public List<T> getListByWhere(@Param("param") Map<String, Object> whereparam, @Param("cls") Class<T> cls);

    /**
     * 标题: getCount
     * 描述: TODO(这里用一句话描述这个方法的作用)
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-15
     */
    @SelectProvider(type = DynaSqlProvider.class, method = "queryPageCount")
    public int getCount(@Param("param") QueryListParam param, @Param("cls") Class<T> cls);

    @UpdateProvider(type = DynaSqlProvider.class, method = "delEntity")
    public int delEntity(T t);

    /**
     *    标题: createTable
     * 描述: 创建表
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-15
     */
    @UpdateProvider(type = DynaSqlProvider.class, method = "createTable")
    public void createTable(Class<?> cls);


    /**
     * 标题: checkIsExsitTable
     * 描述: 查询表是否已存在
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-15
     */
    @Select("select count(table_name) FROM information_schema.TABLES WHERE table_name=#{table_name}")
    public int checkIsExsitTable(String table_name);

    /**
     * 标题: dropTable
     * 描述: 删除表
     * 作者 : wzy
     * 版本号: 1.0
     * 日期 2017-9-15
     */
    @Update("drop table ${table_name}")
    public void dropTable(@Param("table_name") String table_name);

    /**
     * 查询出指定的字段
     *
     * @author Lee
     * 20190408
     */
    @Select("SELECT ${sel_key} FROM  ${table_name} WHERE ${where}")
    public List<String> getValueByKey(@Param("table_name") String table_name, @Param("sel_key") String sel_key, @Param("where") String whereString);


}
