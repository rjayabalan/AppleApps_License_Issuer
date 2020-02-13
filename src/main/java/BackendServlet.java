/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.rmi.CORBA.Util;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.sun.org.apache.xpath.internal.operations.Bool;

import models.DBEntry;
import utils.JacksonParser;
import utils.UtilHibernate;
import utils.UtilHttpClient;
import utils.UtilJobs;

@WebServlet("/api")
public class BackendServlet extends HttpServlet
{
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("Nothing here Byebye!");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        String job = req.getParameter("job").trim();
        String serialId = req.getParameter("serialId").trim();
        String appId = req.getParameter("appId").trim();
        //out.println("Hello WOrld! : " + job + " ~ " + serialId + " ~ " +  appId);

        String sToken = "?sToken=eyJleHBEYXRlIjoiMjAyMC0wOS0xNVQyMzoxNToxNi0wNzAwIiwidG9rZW4iOiI5c3RtRTcvbWFuckxJZExqcU1DeXpjaXM2S1BxZ0p3blVha1JMditVN0swdlF1RTQvWDIwdkNYeXd2U3pwZXpZQk05d3B0M0Z0bVYrSExXYldlcVRWdUhmaWxzL050ajZ1OTgzdktPckFjbkNBOHlvN0VDV09IQ1o3bm1kSDFMK09zVzdJeThUVlZ5MkNWS0JXZGVOZEE9PSIsIm9yZ05hbWUiOiJOb3ZlbGwifQ==";

        //Helper Classes
        UtilHttpClient httpHelper = new UtilHttpClient();
        JacksonParser parserHelper = new JacksonParser();
        UtilJobs jobHelper = new UtilJobs();

        out.println("<h1>AdamStr : " + appId + "</h1><br>");


        DBEntry entry = new DBEntry();
        entry.setSerialId(serialId);
        entry.setAppId(appId);
        entry.setStatus(Boolean.FALSE);

        if(!jobHelper.alreadyExists(entry)){
            jobHelper.addEntry(entry);
        }

        if(job.equals("get")){

            String url = "https://vpp.itunes.apple.com/mdm/manageVPPLicensesByAdamIdSrv" + sToken +
                    "&associateSerialNumbers="+ serialId +
                    "&adamIdStr="+ appId +
                    "&pricingParam=STDQ";

            String response = httpHelper.get(url.trim());
            int status = parserHelper.getStatus(response.getBytes());

            if(status == 0) {
                Session session = UtilHibernate.getSessionFactory().openSession();
                session.beginTransaction();

                Query query = session.createQuery("update DBEntry set status=:status where serialId=:serialId and appId=:appId");

                query.setParameter("status",Boolean.TRUE);
                query.setParameter("serialId",serialId);
                query.setParameter("appId",appId);

                int modification = query.executeUpdate();
                out.println("<h1>" + modification + "</h1><br>");
                session.getTransaction().commit();

                out.println("<script type=\"text/javascript\">");
                //out.println("alert('Associated');");
                out.println("location='index.jsp?status=success&message=App%20Successfully%20Associated';");
                out.println("</script>");
            }
            else{
                String errMsg = parserHelper.getErrMsg(response.getBytes());

                if(errMsg == null || errMsg.length() == 0) {
                    errMsg = parserHelper.getNestedErrMsgAsso(response.getBytes());
                }

                out.println("<script type=\"text/javascript\">");
                //out.println("alert('Failed');");
                out.println("location='index.jsp?status=failure&message=" + errMsg + "';");
                out.println("</script>");
            }

        }
        else if(job.equals("del")){

            String url = "https://vpp.itunes.apple.com/mdm/manageVPPLicensesByAdamIdSrv" + sToken +
                    "&disassociateSerialNumbers="+ serialId +
                    "&adamIdStr="+ appId +
                    "&pricingParam=STDQ";

            String response = httpHelper.get(url.trim());
            int status = parserHelper.getStatus(response.getBytes());
            if(status == 0){

                //Modify Data in database
                Session session = UtilHibernate.getSessionFactory().openSession();
                session.beginTransaction();

                Query query = session.createQuery("update DBEntry set status=:status where serialId=:serialId and appId=:appId");

                query.setParameter("status",Boolean.FALSE);
                query.setParameter("serialId",serialId);
                query.setParameter("appId",appId);

                int modification = query.executeUpdate();
                session.getTransaction().commit();
                out.println("<script type=\"text/javascript\">");
               // out.println("alert('Associated');");
                out.println("location='index.jsp?status=success&message=App%20Successfully%20Dis-associated';");
                out.println("</script>");
            }
            else{

                out.println(response);
                String errMsg = parserHelper.getErrMsg(response.getBytes());
                out.println(errMsg);
                if(errMsg == null || errMsg.length() == 0) {
                    errMsg = parserHelper.getNestedErrMsgDisAsso(response.getBytes());
                    out.println(errMsg);
                }

                out.println("<script type=\"text/javascript\">");
                //out.println("alert('Failed');");
                out.println("location='index.jsp?status=failure&message="+errMsg+"';");
                out.println("</script>");
            }
        }

        else{

            if(jobHelper.alreadyExists(entry)){
                Session session = UtilHibernate.getSessionFactory().openSession();
                session.beginTransaction();

                Query query = session.createQuery("from DBEntry where serialId=:serialId and appId=:appId");

                query.setParameter("serialId",serialId);
                query.setParameter("appId",appId);

                DBEntry tempentry  =  (DBEntry)  query.uniqueResult();

               // out.println("<h4>Status : " + tempentry.isStatus() + "</h4>");
                if(tempentry.isStatus()) {
                    out.println("<script type=\"text/javascript\">");
                    // out.println("alert('Associated');");
                    out.println("location='index.jsp?status=success&message=Association%20Status%20:%20True';");
                    out.println("</script>");
                }
                else{
                    //String errMsg = parserHelper.getNestedErrMsg(response.getBytes());
                    out.println("<script type=\"text/javascript\">");
                    //out.println("alert('Failed');");
                    out.println("location='index.jsp?status=failure&message=Association%20Status%20:%20False';");
                    out.println("</script>");
                }

            }
            else{
                String url = " https://vpp.itunes.apple.com/mdm/getVPPLicensesSrv" + sToken +
                        "&serialNumbers="+ serialId +
                        "&adamIdStr="+ appId;

                String response = httpHelper.get(url.trim());
                out.println(response+ "<br>");
                String status = parserHelper.getNestedStatus(response.getBytes());
                out.println("Status : " + status + "<br>");
                if(status.equals("Associated")){
                    out.println("<script type=\"text/javascript\">");
                    // out.println("alert('Associated');");
                    out.println("location='index.jsp?status=success&message=Association%20Status%20:%20True';");
                    out.println("</script>");
                }
                else{
                    //String errMsg = parserHelper.getNestedErrMsg(response.getBytes());
                    out.println("<script type=\"text/javascript\">");
                    //out.println("alert('Failed');");
                    out.println("location='index.jsp?status=failure&message=Association%20Status%20:%20False';");
                    out.println("</script>");
                }
            }
        }
    }
}
