package protobuf.protest;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.google.protobuf.InvalidProtocolBufferException;

public class Test {
	public static void main(String[] args) {

		// 序列化过程
		// FirstProtobuf是生成类的名字，即proto文件中的java_outer_classname
		// testBuf是里面某个序列的名字，即proto文件中的message testBuf
		FirstProtobuf.testBuf.Builder builder = FirstProtobuf.testBuf.newBuilder();
		builder.setID(777);
		builder.setUrl("shiqi");

		// testBuf
		FirstProtobuf.testBuf info = builder.build();

		byte[] result = info.toByteArray();

		/*String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@10.64.59.12:1521/orcl";
		String user = "parkingsystem";
		String password = "parkingsystem";
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, password);

			if (!conn.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");

				// 此处只能使用prepareStatement
				PreparedStatement ps = conn.prepareStatement("insert into test(id,test) values (1,?)");

				// 写入数据库，要把它改写为流的形式
				ByteArrayInputStream stream = new ByteArrayInputStream(result);
				ps.setBinaryStream(1, stream, stream.available());
				Statement statement = conn.createStatement();

				Blob blob = null;
				ps.execute();

				// //////////////上述完成将写入数据库的操作，数据库中对应的字段的属性要设置为Blob

				String sql = "select test from test";
				ResultSet rs = statement.executeQuery(sql);
				if (rs.next()) {
					blob = rs.getBlob("test");
				}

				byte[] s = blob.getBytes(1, (int) blob.length());

				FirstProtobuf.testBuf testBuf = FirstProtobuf.testBuf.parseFrom(s);
				System.out.println(testBuf);
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		// 反序列化过程
		try {
			FirstProtobuf.testBuf testBuf = FirstProtobuf.testBuf.parseFrom(result);
			System.out.println(testBuf);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

	}
}