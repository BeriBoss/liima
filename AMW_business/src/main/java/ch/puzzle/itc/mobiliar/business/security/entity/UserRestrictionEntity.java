/*
 * AMW - Automated Middleware allows you to manage the configurations of
 * your Java EE applications on an unlimited number of different environments
 * with various versions, including the automated deployment of those apps.
 * Copyright (C) 2013-2016 by Puzzle ITC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.puzzle.itc.mobiliar.business.security.entity;

import ch.puzzle.itc.mobiliar.business.database.control.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Table(name = "TAMW_userRestriction")
public class UserRestrictionEntity implements Serializable
{

	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	@TableGenerator(name = "userrestrictionIdGen", table = Constants.GENERATORTABLE, pkColumnName = Constants.GENERATORPKCOLUMNNAME,
			valueColumnName = Constants.GENERATORVALUECOLUMNNAME, pkColumnValue = "userrestrictionId")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "userrestrictionIdGen")
	@Id
	@Column(unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(unique = true)
	private String name;

	@Getter
	@Version
	private long v;

	public UserRestrictionEntity() {
		// Used by JPA
	}

	public UserRestrictionEntity(String name) {
		this.name = name;
	}
}
