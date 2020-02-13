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

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonParser
{

    private ObjectMapper objectMapper = new ObjectMapper();

    public int getStatus(byte[] jsonData) throws IOException
    {
        JsonNode rootnode = objectMapper.readTree(jsonData);

        // read JSON like DOM Parser
        JsonNode tempnode = rootnode.path("status");

        return tempnode.asInt();
    }

    public String getNestedStatus(byte[] jsonData) throws IOException
    {
        JsonNode rootnode = objectMapper.readTree(jsonData);

        // read JSON like DOM Parser
        JsonNode tempnode = rootnode.path("licenses");

        return tempnode.get(0).path("status").asText();

    }

    public String getNestedErrMsgAsso(byte[] jsonData) throws IOException
    {
        JsonNode rootnode = objectMapper.readTree(jsonData);

        // read JSON like DOM Parser
        JsonNode tempnode = rootnode.path("associations");

        return tempnode.get(0).path("errorMessage").asText();

    }

    public String getNestedErrMsgDisAsso(byte[] jsonData) throws IOException
    {
        JsonNode rootnode = objectMapper.readTree(jsonData);

        // read JSON like DOM Parser
        JsonNode tempnode = rootnode.path("disassociations");

        return tempnode.get(0).path("errorMessage").asText();

    }

    public String getErrMsg(byte[] jsonData) throws IOException
    {
        JsonNode rootnode = objectMapper.readTree(jsonData);

        // read JSON like DOM Parser
        JsonNode tempnode = rootnode.path("errorMessage");

        if(!tempnode.isNull()) {
            return tempnode.asText();
        }

        return null;
    }





}
