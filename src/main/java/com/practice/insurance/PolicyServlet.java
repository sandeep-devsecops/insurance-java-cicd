package com.practice.insurance;
import java.io.*;import java.sql.*;import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;
@WebServlet("/")
public class PolicyServlet extends HttpServlet {
 private Connection db() throws SQLException {
  String host=System.getenv().getOrDefault("DB_HOST","localhost");
  String user=System.getenv().getOrDefault("DB_USER","insurance");
  String pass=System.getenv().getOrDefault("DB_PASSWORD","insurancepass");
  return DriverManager.getConnection("jdbc:mysql://"+host+":3306/insurance?useSSL=false&allowPublicKeyRetrieval=true",user,pass);
 }
 private String escape(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
 protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException {
  resp.setContentType("text/html;charset=UTF-8");PrintWriter o=resp.getWriter();
  o.println("<html><head><title>Insurance Portal</title><style>body{font-family:Arial;max-width:850px;margin:40px auto;background:#eef3fa}main{background:white;padding:25px;border-radius:12px}input,select,button{padding:9px;margin:5px}table{width:100%;border-collapse:collapse}td,th{border-bottom:1px solid #ddd;padding:10px;text-align:left}</style></head><body><main><h1>Insurance Policy Portal</h1><form method='post'><input name='name' placeholder='Customer name' required maxlength='100'><select name='type'><option>Health</option><option>Life</option><option>Vehicle</option></select><input name='premium' type='number' step='0.01' min='0.01' placeholder='Premium' required><button>Add Policy</button></form><h2>Policies</h2><table><tr><th>ID</th><th>Customer</th><th>Type</th><th>Premium</th></tr>");
  try(Connection c=db();PreparedStatement p=c.prepareStatement("SELECT id,customer,type,premium FROM policies ORDER BY id DESC");ResultSet rs=p.executeQuery()){
   while(rs.next())o.println("<tr><td>"+rs.getInt(1)+"</td><td>"+escape(rs.getString(2))+"</td><td>"+escape(rs.getString(3))+"</td><td>"+rs.getBigDecimal(4)+"</td></tr>");
  }catch(SQLException e){getServletContext().log("DB unavailable",e);o.println("<tr><td colspan='4'>Database unavailable. Check logs.</td></tr>");}
  o.println("</table></main></body></html>");
 }
 protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException {
  String name=req.getParameter("name"),type=req.getParameter("type");double premium;
  try{premium=Double.parseDouble(req.getParameter("premium"));}catch(Exception e){resp.sendError(400);return;}
  if(!PolicyValidator.valid(name,type,premium)){resp.sendError(400);return;}
  try(Connection c=db();PreparedStatement p=c.prepareStatement("INSERT INTO policies(customer,type,premium) VALUES(?,?,?)")){
   p.setString(1,name.trim());p.setString(2,type);p.setDouble(3,premium);p.executeUpdate();
  }catch(SQLException e){getServletContext().log("DB insert failed",e);resp.sendError(503);return;}
  resp.sendRedirect(req.getContextPath()+"/");
 }
}