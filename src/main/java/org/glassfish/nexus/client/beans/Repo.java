/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2014 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.nexus.client.beans;

import java.util.Set;
import javax.xml.bind.annotation.XmlType;
import org.glassfish.nexus.client.NexusClientException;
import org.glassfish.nexus.client.NexusClientImpl;
import org.glassfish.nexus.client.StagingAggregation;
import org.glassfish.nexus.client.StagingOperation;

/**
 *
 * @author Romain Grecourt
 */
@XmlType
final public class Repo implements StagingOperation {
    private String id;
    private String name;
    private String repoType;
    private String writePolicy;

    public Repo() {
    }

    public Repo(StagingProfileRepo repo) {
        this.id = repo.getRepositoryId();
        this.name = repo.getRepositoryName();
        this.repoType = repo.getProfileType();
        if (repo.isOpen()) {
            this.writePolicy = "ALLOW_WRITE";
        } else {
            this.writePolicy = "READ_ONLY";
        }
    }

    public String getWritePolicy() {
        return writePolicy;
    }

    public void setWritePolicy(String writePolicy) {
        this.writePolicy = writePolicy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepoType() {
        return repoType;
    }

    public void setRepoType(String repoType) {
        this.repoType = repoType;
    }

    @Override
    public String toString() {
        return "Repository{" + "id=" + id + ", name=" + name + '}';
    }
    
    public boolean isOpen(){
        return writePolicy.equals("ALLOW_WRITE");
    }

    @Override
    public void close(String msg) throws NexusClientException {
        getNexusClient().closeStagingRepo(msg, getIds());
    }

    @Override
    public void drop(String msg) throws NexusClientException {
        getNexusClient().dropStagingRepo(msg, getIds());
    }

    @Override
    public Repo promote(String profile, String msg) throws NexusClientException {
        return getNexusClient().promoteStagingRepo(profile, msg, getIds());
    }

    @Override
    public StagingAggregation aggregate(StagingOperation repo) throws NexusClientException {
        return new StagingAggregation(this).aggregate(repo);
    }

    @Override
    public String[] getIds() {
        return new String[]{getId()};
    }

    public Set<MavenArtifactInfo> getContent(){
        return getNexusClient().getArtifactsInRepo(getId());
    }

    private StagingProfileRepo getProfileRepo(){
        return getNexusClient().getStagingProfileRepo(getId());
    }

    private static NexusClientImpl getNexusClient(){
        return ((NexusClientImpl)NexusClientImpl.getInstance());
    }

    public Repo getParent(){
        Repo parent = null;
        StagingProfileRepo profileRepo;
        if((profileRepo = getProfileRepo()) != null
                && profileRepo.getParentGroupId() != null){
            return new Repo(getNexusClient().getStagingProfileRepo(profileRepo.getParentGroupId()));
        }
        return parent;
    }

    public String getProfileName() {
        StagingProfileRepo profileRepo;
        if((profileRepo =
                getNexusClient().getStagingProfileRepo(getId())) != null){
            return profileRepo.getProfileName();
        }
        return null;
    }

    public boolean isGroup(){
        StagingProfileRepo profileRepo;
        if((profileRepo = getProfileRepo()) != null){
            return profileRepo.getProfileType().equals("group");
        }
        return false;
    }

    public Set<Repo> getGroupTree(){
        if(isGroup()){
            return getNexusClient().getGroupTree(getId());
        }
        return null;
    }

    public void close(
            String msg,
            int retryCount,
            long timeout) throws NexusClientException {

          getNexusClient().closeStagingRepo(
                  msg,
                  getIds(),
                  retryCount,
                  timeout);
  }

    public void drop(
            String msg,
            int retryCount,
            long timeout) throws NexusClientException {

          getNexusClient().dropStagingRepo(
                  msg,
                  getIds(),
                  retryCount,
                  timeout);
  }

    public Repo promote(
            String profile,
            String msg,
            int retryCount,
            long timeout) throws NexusClientException {

          return getNexusClient().promoteStagingRepo(
                  profile,
                  msg,
                  getIds(),
                  retryCount,
                  timeout);
  }
}