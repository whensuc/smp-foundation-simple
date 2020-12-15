package cn.com.dyg.work.mybatis;



import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import cn.com.dyg.work.annotation.DBField;
import cn.com.dyg.work.annotation.ZColumn;
import cn.com.dyg.work.annotation.ZTable;
import cn.com.dyg.work.base.BaseEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;


/**
 * <b>动态sql
 */
public class DynaSqlProvider<T extends BaseEntity> {

	/**
	 *
	 * 标题: insertEntity
	 * 描述: 返回插入实体类sql <b>
	 * 通过反射获取类属性，不包括id主键和@DBField(isIgnore=true)注解的属性
	 * 作者 : wzy
	 * 版本号: 1.0
	 * 日期:2017-9-13
	 */
	public String insertEntity(final T t) {

		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = t.getClass();
		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}

		String sql = new SQL() {
			{
				INSERT_INTO(t.getClass().getAnnotation(ZTable.class).tableName());
				for (Field fd : fieldList) {
					if (!fd.isAnnotationPresent(DBField.class)||(fd.isAnnotationPresent(DBField.class)&&!fd.getAnnotation(DBField.class).isIgnore()))
						VALUES(fd.getName(), "#{" + fd.getName() + "}");
				}

			}
		}.toString();
		return sql;
	}

	/**
	 *
	 * 标题: insertEntity_batch
	 * 描述: 通过拼接方式实现批量插入，该种插入方法一次插入数据小于千条，过多会出错
	 * <b>超过千条数据采用JdbcTemplate的批量插入或开启mybatis批量模式
	 * 作者 : wzy
	 * 版本号: 1.0
	 * 日期 :2017-9-13
	 */
	public String insertEntity_batch(final Map<String, List<T>> param)
			throws Exception {
		final List<T> list = (List<T>) param.get("list");
		if (list.size() == 0)
			return null;
		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = list.get(0).getClass();

		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		StringBuilder cols = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		sql.append("insert into " + cls.getAnnotation(ZTable.class).tableName() + "(");
		cols.append("(");
		for (Field fd : fieldList) {
			if (!fd.isAnnotationPresent(DBField.class)||(fd.isAnnotationPresent(DBField.class)&&!fd.getAnnotation(DBField.class).isIgnore())) {
				sql.append(fd.getName() + ",");
				cols.append(MessageFormat.format("#'{'list[{0}].{1}'}'",
						fd.getName()));
				cols.append(",");
			}

		}
		cols.deleteCharAt(cols.length() - 1);
		cols.append(")");
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") values");
		for (int i = 0; i < list.size(); i++) {
			sql.append("(");
			for (Field fd : fieldList) {
				if (!fd.isAnnotationPresent(DBField.class)||(fd.isAnnotationPresent(DBField.class)&&!fd.getAnnotation(DBField.class).isIgnore())) {
					sql.append(MessageFormat.format("#'{'list[{0}].{1}'}'", i,
							fd.getName()));
					sql.append(",");
				}
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
			if (i < list.size() - 1)
				sql.append(",");
		}
		return sql.toString();
	}

	/**
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String insertEntity_batch_oracle(final Map<String, List<T>> param)
			throws Exception {
		final List<T> list = (List<T>) param.get("list");
		if (list.size() == 0)
			return null;
		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = list.get(0).getClass();

		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		StringBuilder cols = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		sql.append("insert into " + cls.getAnnotation(ZTable.class).tableName() + "(");
		cols.append("(");
		for (Field fd : fieldList) {
			if (!fd.isAnnotationPresent(DBField.class)||(fd.isAnnotationPresent(DBField.class)&&!fd.getAnnotation(DBField.class).isIgnore())) {
				sql.append(fd.getName() + ",");
				cols.append(MessageFormat.format("#'{'list[{0}].{1}'}'",
						fd.getName()));
				cols.append(",");
			}

		}
		cols.deleteCharAt(cols.length() - 1);
		cols.append(")");
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ");
		for (int i = 0; i < list.size(); i++) {
			sql.append(" select ");
			for (Field fd : fieldList) {
				if (!fd.isAnnotationPresent(DBField.class)||(fd.isAnnotationPresent(DBField.class)&&!fd.getAnnotation(DBField.class).isIgnore())) {
					sql.append(MessageFormat.format("#'{'list[{0}].{1}'}'", i,
							fd.getName()));
					sql.append(",");
				}
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" from dual ");
			if (i < list.size() - 1)
				sql.append(" union all");
		}
		return sql.toString();
	}

	/**
	 *
	 * 标题: updateEntity
	 * 描述: 更新实体类，不更新注解 @DBField(isIgnore=true)的属性、主键以及值为空的字段
	 * 作者: wzy
	 * 版本号: 1.0
	 * 日期 :2017-9-14
	 */
	public String updateEntity(final T t) throws Exception {

		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = t.getClass();
		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}

		return new SQL() {
			{
				String primary = "";
				UPDATE(t.getClass().getAnnotation(ZTable.class).tableName());
				for (Field fd : fieldList) {
					if(fd.isAnnotationPresent(ZColumn.class)&&fd.getAnnotation(ZColumn.class).isPrimary())primary = fd.getName();
					if (!"id".equals(fd.getName())
							&& !fd.isAnnotationPresent(DBField.class)&&!fd.getName().equals(primary)) {
						PropertyDescriptor pd = new PropertyDescriptor(
								fd.getName(), t.getClass());
						Method getMethod = pd.getReadMethod();
						if (getMethod != null) {
							Object o = getMethod.invoke(t);
							if (o != null)
								SET(fd.getName() + "=#{" + fd.getName() + "}");
						}
					}
					if(fd.isAnnotationPresent(DBField.class)&&fd.getAnnotation(DBField.class).isWhere()){
						WHERE(fd.getName() + "=#{" + fd.getName() + "}");
					}
				}
			}
		}.toString();
	}

	public String updateEntity_batch(final Map<String, List<T>> param) throws Exception {
		final List<T> list = (List<T>) param.get("list");
		if (list.size() == 0)
			return null;
		final List<Field> fieldList = new ArrayList<>();
		Class<?> cls = list.get(0).getClass();

		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		StringBuilder sql = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			String where = "";
			sql.append("update ").append(cls.getAnnotation(ZTable.class).tableName()).append(" set ");
			String primary = "";
			for (Field fd : fieldList) {
				if(fd.isAnnotationPresent(ZColumn.class)&&fd.getAnnotation(ZColumn.class).isPrimary())primary=fd.getName();
				if (!fd.isAnnotationPresent(DBField.class) && !fd.getName().equals(primary)) {
					sql.append(MessageFormat.format(fd.getName() + "= #'{'list[{0}].{1}'}'", i, fd.getName()));
					sql.append(",");
				}
				if (fd.isAnnotationPresent(DBField.class)&&fd.getAnnotation(DBField.class).isWhere())
					where = MessageFormat.format(fd.getName() + "= #'{'list[{0}].{1}'}'", i, fd.getName());

			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" where ").append(where);
			if (i < list.size() - 1)
				sql.append(";");
		}
		return sql.toString();
	}


	/**
	 *
	 * 标题: editEntity
	 * 描述: 更新实体类，除注解 @DBField(isIgnore=true)的属性和主键外所有字段都更新
	 * 作者 :wzy
	 * 版本号: 1.0
	 * 日期 :2017-9-14
	 */
	public String editEntity(final T t) {
		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = t.getClass();
		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		SQL sql = new SQL();
		sql.UPDATE(t.getClass().getAnnotation(ZTable.class).tableName());
		String primary = "";
		for (Field fd : fieldList) {
			if(fd.isAnnotationPresent(ZColumn.class)&&fd.getAnnotation(ZColumn.class).isPrimary())primary=fd.getName();
			if (!fd.isAnnotationPresent(DBField.class) && !fd.getName().equals(primary)) {
				sql.SET(fd.getName() + "=#{" + fd.getName() + "}");
			}else if(fd.isAnnotationPresent(DBField.class)&&fd.getAnnotation(DBField.class).isWhere()){
				sql.WHERE(fd.getName() + "=#{" + fd.getName() + "}");
			}
		}

		return sql.toString();
	}

	/**
	 *
	 * 标题: delEntity <br/>
	 * 描述: 删除<br/>
	 * 作者 : wzy<br/>
	 * 版本号: 1.0<br/>
	 * 日期： 2018-6-3<br/>
	 */
	public String delEntity(final T t) {
		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = t.getClass();
		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		SQL sql = new SQL();
		sql.DELETE_FROM(t.getClass().getAnnotation(ZTable.class).tableName());
		for (Field fd : fieldList) {
			if(fd.isAnnotationPresent(DBField.class)&&fd.getAnnotation(DBField.class).isWhere()){
				sql.WHERE(fd.getName() + "=#{" + fd.getName() + "}");
			}
		}

		return sql.toString();
	}

	/**
	 *
	 * 标题: queryListAll
	 * 描述: 查询实体类对应表的所有数据
	 * 作者 : wzy
	 * 版本号: 1.0
	 * 日期 :2017-9-15
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public String queryListAll(final Class<T> t) throws InstantiationException, IllegalAccessException {
		return new SQL() {
			{
				SELECT("*");
				FROM(t.getClass().getAnnotation(ZTable.class).tableName());
			}
		}.toString();
	}

	public String queryListByWhere(@Param("param") final Map<String,Object> whereparam, @Param("cls") final Class<T> t) throws Exception {
		SQL sql = new SQL();
		sql.SELECT("*");
		sql.FROM(t.getClass().getAnnotation(ZTable.class).tableName());
		for(Map.Entry<String, Object> entry:whereparam.entrySet()){
			sql.WHERE(entry.getKey()+"#{param["+entry.getKey()+"]}");
		}
		return sql.toString();
	}

	/**
	 *
	 * 标题: queryListPage
	 * 描述: 分页查询数据
	 * 作者 : wzy
	 * 版本号: 1.0
	 * 日期 :2017-9-15
	 */
	public String queryListPage_MSSQL(@Param("param") QueryListParam  param,@Param("cls") Class<T> cls)
			throws InstantiationException, IllegalAccessException {
		StringBuilder builder = new StringBuilder("select top "
				+ param.getPagenum() + " * from ");
		builder.append("(select ROW_NUMBER() over (ORDER BY "
				+ (param.getOrdercol() == null ? "ts" : param.getOrdercol())
				+ " desc) as rowNumber,* from "
				+ cls.getAnnotation(ZTable.class).tableName() + " where isnull(dr,0)=0");
		Map<String, Object> whereparam = param.getWhereparam();
		if (whereparam != null) {
			for (Map.Entry<String, Object> entry : whereparam.entrySet()) {
				builder.append(" and " + entry.getKey() +"#{param.whereparam["+entry.getKey()+"]}");
			}
		}
		builder.append(")as a where a.rowNumber > " + param.getPagenum() + "*("
				+ param.getPageindex() + "-1)");
		return builder.toString();
	}

	public String queryList_MSSQL(Class<T> cls,int index,int number,String where,String order)
			throws InstantiationException, IllegalAccessException {
		StringBuilder builder = new StringBuilder("select top "
				+ number + " * from ");
		builder.append("(select ROW_NUMBER() over (ORDER BY "
				+ (order == null ? "ts" : order)
				+ " desc) as rowNumber,* from "
				+ cls.getAnnotation(ZTable.class).tableName() + " where isnull(dr,0)=0 ");
		if (where != null) {
			builder.append(" and " + where);
		}
		builder.append(")as a where a.rowNumber > " + number*(index-1));
		return builder.toString();
	}

	public String queryListPage_MYSQL(@Param("param") QueryListParam param,@Param("cls") Class<T> cls)
			throws InstantiationException, IllegalAccessException {
		StringBuilder builder = new StringBuilder();
		SQL sql = new SQL();
		sql.SELECT("*");
		sql.FROM(cls.getAnnotation(ZTable.class).tableName());
		Map<String, Object> whereparam = param.getWhereparam();
		if (whereparam != null) {
			for (Map.Entry<String, Object> entry : whereparam.entrySet()) {
				sql.WHERE(entry.getKey() +" #{param.whereparam["+entry.getKey()+"]}");
			}
		}
		if(!StringUtils.isEmpty(param.getWheresql())){
			sql.WHERE(param.getWheresql());
		}
		if(param.getOrdercol()!=null)sql.ORDER_BY(param.getOrdercol());
		builder.append(sql.toString());
		builder.append(" LIMIT "+(param.getPageindex()-1)*param.getPagenum()+","+param.getPagenum());
		return builder.toString();
	}

	public String queryList_MYSQL(@Param("param") Map<String,Object> param,@Param("cls") Class<T> cls)
			throws InstantiationException, IllegalAccessException {
		StringBuilder builder = new StringBuilder();
		SQL sql = new SQL();
		sql.SELECT("*");
		sql.FROM(cls.getAnnotation(ZTable.class).tableName());
		String where = (String)param.get("where");
		if (where != null) {
			sql.WHERE(where);
		}
		String order = (String)param.get("order");
		if(order!=null)sql.ORDER_BY(order);
		builder.append(sql.toString());
		Integer index = (Integer)param.get("pageindex");
		Integer number = (Integer)param.get("pagenum");
		builder.append(" limit "+(index-1)*number+","+number);
		return builder.toString();
	}

	public String queryList(@Param("param") QueryListParam param, @Param("cls") Class<T> cls)
			throws InstantiationException, IllegalAccessException {
		StringBuilder builder = new StringBuilder();
		SQL sql = new SQL();
		sql.SELECT("*");
		sql.FROM(cls.getAnnotation(ZTable.class).tableName());
		Map<String, Object> whereparam = param.getWhereparam();
		if (whereparam != null) {
			for (Map.Entry<String, Object> entry : whereparam.entrySet()) {
				sql.WHERE(entry.getKey() + " #{param.whereparam[" + entry.getKey() + "]}");
			}
		}
		String where = (String) param.getWheresql();
		if (where != null) {
			sql.WHERE(where);
		}
		String order = (String) param.getOrdercol();
		if (order != null) sql.ORDER_BY(order);
		builder.append(sql.toString());
		return builder.toString();
	}

	public String queryListPage_ORACLE(@Param("param") QueryListParam param, @Param("cls") Class<T> cls)
			throws InstantiationException, IllegalAccessException {
		String table = cls.getAnnotation(ZTable.class).tableName();
		StringBuilder builder = new StringBuilder("select * from ");
		builder.append("(select ROW_NUMBER() over (ORDER BY "
				+ (param.getOrdercol() == null ? "ts" : param.getOrdercol())
				+ " desc) as rowNumber,"+table+".* from "
				+table+ " where nvl(dr,0)=0");
		Map<String, Object> whereparam = param.getWhereparam();
		if (whereparam != null) {
			for (Map.Entry<String, Object> entry : whereparam.entrySet()) {
				if(entry.getKey().contains(" like before")){
					builder.append(" and " + entry.getKey().substring(0,entry.getKey().indexOf(" before")) +"'%'||#{param.whereparam["+entry.getKey()+"]}");
				}else if(entry.getKey().contains(" like after")){
					builder.append(" and " + entry.getKey().substring(0,entry.getKey().indexOf(" after")) +"#{param.whereparam["+entry.getKey()+"]}||'%'");
				}else if(entry.getKey().contains(" like all")){
					builder.append(" and " + entry.getKey().substring(0,entry.getKey().indexOf(" all")) +"'%'||#{param.whereparam["+entry.getKey()+"]}||'%'");
				}else if(entry.getKey().contains(" or ")){
					builder.append(" and (" + entry.getKey().substring(0,entry.getKey().indexOf(" or "))+"#{param.whereparam["+entry.getKey()+"]}"+" or "+entry.getKey().substring(entry.getKey().indexOf(" or ")+3)+"#{param.whereparam["+entry.getKey()+"]})");
				}else{
					builder.append(" and " + entry.getKey() +"#{param.whereparam["+entry.getKey()+"]}");
				}

			}
		}

		builder.append(") a where a.rowNumber > " + param.getPagenum() + "*("
				+ param.getPageindex() + "-1) and a.rowNumber <="+param.getPagenum()*param.getPageindex());
		return builder.toString();
	}

	/**
	 *
	 * 标题: queryPageCount
	 * 描述: 查询实体类对应表行数
	 * 作者 : wzy
	 * 版本号: 1.0
	 * 日期 2017-9-15
	 */
	public String queryPageCount(@Param("param") QueryListParam param,@Param("cls") Class<T> cls)
			throws InstantiationException, IllegalAccessException {
		/*StringBuilder builder = new StringBuilder("select count(id) from "
				+ cls.newInstance().getTableName() + " where dr=0 ");*/
		SQL sql = new SQL();
		sql.SELECT("count(1)");
		sql.FROM(cls.getAnnotation(ZTable.class).tableName());
		//sql.WHERE("dr=0");
		Map<String, Object> whereparam = param.getWhereparam();
		if (whereparam != null) {
			for (Map.Entry<String, Object> entry : whereparam.entrySet()) {
				if(entry.getKey().contains(" like before")){
					sql.WHERE( entry.getKey().substring(0,entry.getKey().indexOf(" before")+1) +"'%'||#{param.whereparam["+entry.getKey()+"]}");
				}else if(entry.getKey().contains(" like after")){
					sql.WHERE( entry.getKey().substring(0,entry.getKey().indexOf(" after")+1) +"#{param.whereparam["+entry.getKey()+"]}||'%'");
				}else if(entry.getKey().contains(" like all")){
					sql.WHERE(entry.getKey().substring(0,entry.getKey().indexOf(" all")+1) +"'%'||#{param.whereparam["+entry.getKey()+"]}||'%'");
				}else if(entry.getKey().contains(" or ")){
					sql.WHERE("(" + entry.getKey().substring(0,entry.getKey().indexOf(" or "))+"#{param.whereparam["+entry.getKey()+"]}"+" or "+entry.getKey().substring(entry.getKey().indexOf(" or ")+3)+"#{param.whereparam["+entry.getKey()+"]})");
				}else{
					sql.WHERE(entry.getKey() +"#{param.whereparam["+entry.getKey()+"]}");
				}
			}
		}
		if(!StringUtils.isEmpty(param.getWheresql())){
			sql.WHERE(param.getWheresql());
		}
		return sql.toString();
	}

	/**
	 *
	 * 标题: 更新数据
	 * 描述: 根据传入参数更新特定数据
	 * 作者 : wzy
	 * 版本号: 1.0
	 * 日期 2017-9-15
	 */
	public String updateByWhere(@Param("param") UpdateParam param,@Param("cls") final Class<T> t) throws InstantiationException, IllegalAccessException{
		if(param==null||param.getCols()==null||param.getCols().size()==0)return null;
		SQL sql = new SQL();
		sql.UPDATE(t.getClass().getAnnotation(ZTable.class).tableName());
		for(Map.Entry<String, Object> entry:param.getCols().entrySet()){
			sql.SET(entry.getKey()+"#{param.cols["+entry.getKey()+"]}");
		}
		if(param.getWhereparam()!=null){
			for(Map.Entry<String, Object> entry:param.getWhereparam().entrySet()){
				sql.WHERE(entry.getKey()+"#{param.whereparam["+entry.getKey()+"]}");
			}
		}
		return sql.toString();
	}

	/**
	 * 我们约定，命名为id的字段默认主键自增。采用其他形式的字符型主键命名为pk_xx形式。
	 * 约定大于配置
	 * @param t
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public String createTable(final Class<T> t) throws InstantiationException, IllegalAccessException{
		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = t.newInstance().getClass();
		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		StringBuilder sql = new StringBuilder();
		sql.append("create table "+t.getClass().getAnnotation(ZTable.class).tableName()+"(");
		String primary = "";
		for(Field fd:fieldList){
			if(fd.getAnnotation(ZColumn.class).isPrimary()&&fd.getAnnotation(ZColumn.class).isAuto()){
				if(StringUtils.isEmpty(fd.getAnnotation(ZColumn.class).columnType())){
					sql.append(fd.getName()+" int(11)  NOT NULL AUTO_INCREMENT,");
				}else{
					String columntype = fd.getAnnotation(ZColumn.class).columnType();
					String columnlength = fd.getAnnotation(ZColumn.class).columnLength();
					String type = StringUtils.isEmpty(columnlength)?columntype+" ":columntype+"("+columnlength+") ";
					sql.append(fd.getName()+" "+type+"   NOT NULL AUTO_INCREMENT,");
				}

				primary = fd.getName();
			}else if(fd.getAnnotation(ZColumn.class).isPrimary()){
				String columntype = fd.getAnnotation(ZColumn.class).columnType();
				String columnlength = fd.getAnnotation(ZColumn.class).columnLength();
				String type = StringUtils.isEmpty(columnlength)?columntype:columntype+"("+columnlength+") ";
				sql.append(fd.getName()+" "+type+" NOT NULL ,");
				primary = fd.getName();
			}else{
				if(fd.isAnnotationPresent(ZTable.class)){
					String columntype = fd.getAnnotation(ZColumn.class).columnType();
					String columnlength = fd.getAnnotation(ZColumn.class).columnLength();
					String defaultValue = fd.getAnnotation(ZColumn.class).defaultValue();
					String type = StringUtils.isEmpty(columnlength)?columntype+" ":columntype+"("+columnlength+") ";
					sql.append(fd.getName()+" "+type+defaultValue+",");
				}
			}
		}
		sql.append("PRIMARY KEY ("+primary+")");
		sql.append(")");
		return sql.toString();
	}

	public String createTable_ORACLE(final Class<T> t) throws InstantiationException, IllegalAccessException{
		final List<Field> fieldList = new ArrayList<Field>();
		Class<?> cls = t.newInstance().getClass();
		while (cls != null) {
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		StringBuilder sql = new StringBuilder();
		sql.append("create table "+t.getClass().getAnnotation(ZTable.class).tableName()+"(");
		String primary = "";
		for(Field fd:fieldList){
			if(fd.getAnnotation(ZColumn.class).isPrimary()){
				String columntype = fd.getAnnotation(ZColumn.class).columnType();
				String columnlength = fd.getAnnotation(ZColumn.class).columnLength();
				String type = StringUtils.isEmpty(columnlength)?columntype:columntype+"("+columnlength+") ";
				sql.append(fd.getName()+" "+type+" NOT NULL ,");
				primary = fd.getName();
			}else{
				if(fd.isAnnotationPresent(ZColumn.class)){
					String columntype = fd.getAnnotation(ZColumn.class).columnType();
					if(columntype.contains("int"))columntype = "number";
					String columnlength = fd.getAnnotation(ZColumn.class).columnLength();
					String defaultValue = fd.getAnnotation(ZColumn.class).defaultValue();
					String type = StringUtils.isEmpty(columnlength)?columntype+" ":columntype+"("+columnlength+") ";
					sql.append(fd.getName()+" "+type+defaultValue+",");
				}
			}
		}
		sql.append("PRIMARY KEY ("+primary+")");
		sql.append(")");
		return sql.toString();
	}
}
