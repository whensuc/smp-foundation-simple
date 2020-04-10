package cn.com.dyg.work.base;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;


import cn.com.dyg.work.annotation.DBField;
import cn.com.dyg.work.annotation.ZTable;
import cn.com.dyg.work.common.BusinessException;
import cn.com.dyg.work.mybatis.QueryListParam;
import cn.com.dyg.work.mybatis.UpdateParam;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


import cn.com.dyg.work.common.PaginationBO;



public abstract class BaseService<T extends BaseEntity> {

    private static Logger logger = LoggerFactory.getLogger(BaseService.class);

    @Resource
    private JdbcTemplate jdbcTempldate;


    @Autowired
    private BaseDao<T> dao;


    /**
     * 标题: selectList <br/>
     * 描述: 保存对象<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     *
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public int saveEntity(T param) throws BusinessException {
        try {
            int i = dao.saveEntity(param);
            return i;
        } catch (Exception e) {
            logger.error("save error:" + e.getMessage());
            throw new BusinessException("保存失败");
        }
    }

    @Transactional(rollbackFor = BusinessException.class)
    public int insertEntity(T param) throws BusinessException {
        try {
            int i = dao.insertEntity(param);
            return i;
        } catch (Exception e) {
            logger.error("save error:" + e.getMessage());
            throw new BusinessException("保存失败");
        }
    }





    @Transactional(rollbackFor = BusinessException.class)
    public int updateByWhere(UpdateParam param, Class<T> cls) {
        try {
            int i = dao.updateByWhere(param, cls);
            return i;
        } catch (Exception e) {
            logger.error("error:" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 标题: selectList <br/>
     * 描述: 批量保存对象(拼接参数方法)<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void saveEntity_batch(List<T> param) {
        try {
            dao.saveBatchEntity(param);
        } catch (Exception e) {
            logger.error("saveEntity_batch error:" + e.getMessage());
            throw new BusinessException("批量保存失败");
        }
    }

    /**
     * 标题: selectList <br/>
     * 描述: 批量保存对象(拼接参数方法)<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void updateEntity_batch(List<T> param) {
        try {
            dao.updateEntity_batch(param);
        } catch (Exception e) {
            logger.error("updateEntity_batch error:" + e.getMessage());
            throw new BusinessException("批量更新失败");
        }
    }

    /**
     * 标题: selectList <br/>
     * 描述: 批量保存对象jdbc<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void saveEntity_batch_jdbc(final List<T> param) {
        try {
            final List<Field> fieldList = new ArrayList<Field>();
            Class<?> cls = param.get(0).getClass();
            while (cls != null) {
                fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
                cls = cls.getSuperclass(); // 得到父类,然后赋给自己
            }
            final T t = param.get(0);
            final List<Field> fList = new ArrayList<Field>();
            String sql = new SQL() {
                {
                    INSERT_INTO(t.getClass().getAnnotation(ZTable.class).tableName());
                    for (Field fd : fieldList) {
                        if (fd.isAnnotationPresent(ZTable.class) && !fd.getAnnotation(ZTable.class).isAuto()) {
                            VALUES(fd.getName(), "?");
                            fList.add(fd);
                        }

                    }

                }
            }.toString();
            jdbcTempldate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i)
                                throws SQLException {
                            batch_opt(fList,param,ps,i);
                        }

                        @Override
                        public int getBatchSize() {
                            return param.size();
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("saveEntity_batch error:" + e.getMessage());
            throw new BusinessException("批量保存失败");
        }
    }

    /**
     * 标题: selectList <br/>
     * 描述: 批量更新对象jdbc<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void updateEntity_batch_jdbc(final List<T> param) {
        try {
            final List<Field> fieldList = new ArrayList<Field>();
            Class<?> cls = param.get(0).getClass();
            while (cls != null) {
                fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
                cls = cls.getSuperclass(); // 得到父类,然后赋给自己
            }
            final T t = param.get(0);
            final List<Field> fList = new ArrayList<Field>();

            String sql = new SQL() {
                {
                    Field fid = null;
                    UPDATE(t.getClass().getAnnotation(ZTable.class).tableName());
                    for (Field fd : fieldList) {
                        if (fd.isAnnotationPresent(ZTable.class)&&!fd.getAnnotation(ZTable.class).isAuto()) {
                            PropertyDescriptor pd = new PropertyDescriptor(
                                    fd.getName(), t.getClass());
                            Method getMethod = pd.getReadMethod();
                            if (getMethod != null) {
                                Object o = getMethod.invoke(t);
                                if (o != null) {
                                    SET(fd.getName() + "=?");
                                    fList.add(fd);
                                }

                            }

                        }
                        if (fd.isAnnotationPresent(DBField.class) && fd.getAnnotation(DBField.class).isWhere()) {
                            fid = fd;
                        }
                    }
                    WHERE(" id=? ");
                    fList.add(fid);
                }
            }.toString();
            jdbcTempldate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i)
                                throws SQLException {
                            batch_opt(fList,param,ps,i);
                        }

                        @Override
                        public int getBatchSize() {
                            return param.size();
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("updateEntity_batch error:" + e.getMessage());
            throw new BusinessException("批量更新失败");
        }
    }

    private void batch_opt(List<Field> fList,List<T> param,PreparedStatement ps,int i){
        for (int j = 0; j < fList.size(); j++) {
            PropertyDescriptor pd;
            try {
                T pt = param.get(i);
                pd = new PropertyDescriptor(
                        fList.get(j).getName(), pt.getClass());
                Method getMethod = pd.getReadMethod();
                if (getMethod != null) {
                    Object value = getMethod.invoke(pt);
                    ps.setObject(j + 1, value);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }


        }
    }

    /**
     * 标题: selectList <br/>
     * 描述: 删除对象<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void delEntity(T param) {
        try {
            dao.delEntity(param);
        } catch (Exception e) {
            logger.error("delete error:{}" + e);
            throw new BusinessException("删除失败");
        }
    }

    /**
     * 标题: selectList <br/>
     * 描述: 更新对象<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void updateEntity(T param) {
        try {
            dao.updateEntity(param);
        } catch (Exception e) {
            logger.error("update error:{}" + e);
            throw new BusinessException("更新失败");
        }
    }



    /**
     * 标题: selectList <br/>
     * 描述: 查询列表对象<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    public List<T> selectList(QueryListParam param, Class<T> cls) {
        return dao.selectList(param, cls);
    }

    /**
     * 标题: selectOne <br/>
     * 描述: 查询单个对象<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    public T selectOne(QueryListParam param, Class<T> cls) {
        return dao.selectOne(param, cls);
    }


    /**
     * 标题: getListPage_MYSQL <br/>
     * 描述: 分页查询mysql<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    public PaginationBO<T> getListPage_MYSQL(QueryListParam param, Class<T> cls) {
        try {
            List<T> datas = dao.getPageList_MYSQL(param, cls);
            int count = dao.getCount(param, cls);
            PaginationBO<T> pagedata = new PaginationBO<T>(param.getPageindex(), param.getPagenum(), count);
            pagedata.setDatas(datas);
            return pagedata;
        } catch (Exception e) {
            logger.error("getListPage_MYSQL error:{}", e);
            throw new BusinessException("查询失败");
        }


    }

    /**
     * 标题: getListPage_ORACLE <br/>
     * 描述: 分页查询oracle<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    public PaginationBO<T> getListPage_ORACLE(QueryListParam param, Class<T> cls) {
        try {
            List<T> datas = dao.getPageList_ORACLE(param, cls);
            int count = dao.getCount(param, cls);
            PaginationBO<T> pagedata = new PaginationBO<T>(param.getPageindex(), param.getPagenum(), count);
            pagedata.setDatas(datas);
            return pagedata;
        } catch (Exception e) {
            logger.error("getListPage_ORACLE error:{}", e);
            throw new BusinessException("查询失败");
        }

    }

    /**
     * 标题: getListPage_MSSQL <br/>
     * 描述: 分页查询sqlserver<br/>
     * 作者 : wzy<br/>
     * 版本号: 1.0<br/>
     */
    public PaginationBO<T> getListPage_MSSQL(QueryListParam param, Class<T> cls) {
        try {
            List<T> datas = dao.getPageList_MSSQL(param, cls);
            int count = dao.getCount(param, cls);
            PaginationBO<T> pagedata = new PaginationBO<T>(param.getPageindex(), param.getPagenum(), count);
            pagedata.setDatas(datas);
            return pagedata;
        } catch (Exception e) {
            logger.error("getListPage_MSSQL error:{}", e);
            throw new BusinessException("查询失败");
        }

    }

}
