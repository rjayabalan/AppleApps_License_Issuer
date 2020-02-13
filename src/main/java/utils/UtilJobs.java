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
package utils;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import models.DBEntry;

public class UtilJobs
{

    public void addEntry(DBEntry entry){

        //Add Data to database
        Session session = UtilHibernate.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(entry);
        session.getTransaction().commit();

    }

    public boolean alreadyExists(DBEntry entry){

        //Modify Data in database
        Session session = UtilHibernate.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("select 1 from DBEntry where serialId=:serialId and appId=:appId");

        query.setParameter("serialId",entry.getSerialId());
        query.setParameter("appId",entry.getAppId());

        List rowsFound = query.list();
        session.getTransaction().commit();

        if(rowsFound.size() != 0){
            return true;
        }
        return false;
    }


}
