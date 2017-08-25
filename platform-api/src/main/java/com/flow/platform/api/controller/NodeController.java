/*
 * Copyright 2017 flow.ci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flow.platform.api.controller;

import com.flow.platform.api.domain.node.Node;
import com.flow.platform.api.service.node.NodeService;
import com.flow.platform.api.util.PathUtil;
import com.flow.platform.core.exception.IllegalParameterException;
import com.flow.platform.core.exception.NotFoundException;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yang
 */
public abstract class NodeController {

    @Autowired
    protected NodeService nodeService;

    /**
     * @api {get} /flows/env Get Env
     * @apiParam {String} pathOrName node path or name
     * @apiParam {String} [key] env variable name
     * @apiGroup Node
     * @apiDescription Get node env by path or name
     *
     * @apiSuccessExample {json} Success-Response
     *  {
     *      FLOW_ENV_VAR: xxx
     *  }
     */
    @GetMapping("/env")
    public Map<String, String> getEnv(@RequestParam String pathOrName, @RequestParam(required = false) String key) {
        String path = pathOrName;

        // check is path for root name
        if (PathUtil.isRootName(path)) {
            path = PathUtil.build(path);
        }

        Node node = nodeService.find(path);

        if (node == null) {
            throw new IllegalParameterException("Invalid node path");
        }

        if (Strings.isNullOrEmpty(key)) {
            return node.getEnvs();
        }

        String env = node.getEnv(key);
        if (Strings.isNullOrEmpty(env)) {
            throw new NotFoundException("Env key is not exist");
        }

        Map<String, String> singleEnv = new HashMap<>(1);
        singleEnv.put(key, env);
        return singleEnv;
    }
}