/*
 * Copyright 2002-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perfxq.unit;

import java.sql.SQLException;

import org.dbunit.database.IDatabaseConnection;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Holds a number of {@link IDatabaseConnection} beans.
 *
 * @author huayq
 */
public class DatabaseConnections {

	private final String[] names;

	private final IDatabaseConnection[] connections;

	public DatabaseConnections(String[] names, IDatabaseConnection[] connections) {
		Assert.notEmpty(names, "Names must not be empty");
		Assert.notEmpty(connections, "Connections must not be empty");
		Assert.isTrue(names.length == connections.length, "Names and Connections must have the same length");
		this.names = names;
		this.connections = connections;
	}

	public void closeAll() throws SQLException {
		for (IDatabaseConnection connection : this.connections) {
			connection.close();
		}
	}

	public IDatabaseConnection getDefault() {
		return this.connections[0];
	}

	public IDatabaseConnection get(String name) {
		if (!StringUtils.hasLength(name)) {
			return this.connections[0];
		}
		for (int i = 0; i < this.names.length; i++) {
			if (this.names[i].equals(name)) {
				return this.connections[i];
			}
		}
		throw new IllegalStateException("Unable to find connection named " + name);
	}

}
