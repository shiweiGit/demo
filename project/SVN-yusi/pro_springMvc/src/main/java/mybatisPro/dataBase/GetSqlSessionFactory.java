package mybatisPro.dataBase;

//import java.io.File;
//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <P>
 * 获取sqlsessionfactory对象
 * 
 * @author si.yu
 */
public class GetSqlSessionFactory {

	private static SqlSessionFactory sqlsessionfactory;

	private static final String MYSQL_ENVIRONMENT_ID = "mysql";

	// private static final Logger
	// logger=LoggerFactory.getLogger(GetSqlSessionFactory.class);

	private GetSqlSessionFactory() {
	}

	public static synchronized SqlSessionFactory getInstance() throws IOException {
		if (null == sqlsessionfactory) {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classloader.getResourceAsStream("config.xml");

			// File file=new File("./config/config.xml");
			// InputStream inputStream=new FileInputStream(file);

			sqlsessionfactory = new SqlSessionFactoryBuilder().build(inputStream, MYSQL_ENVIRONMENT_ID);
		}
		return sqlsessionfactory;
	}

}