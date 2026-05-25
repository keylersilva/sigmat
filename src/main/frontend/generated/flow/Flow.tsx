/*
 * Copyright 2000-2024 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/// <reference lib="es2018" />
import { Flow as _Flow } from "Frontend/generated/jar-resources/Flow.js";
import React, { useCallback, useEffect, useRef } from "react";
import {
    matchRoutes,
    useBlocker,
    useLocation,
    useNavigate
} from "react-router-dom";
import type { AgnosticRouteObject } from '@remix-run/router';

const flow = new _Flow({
    imports: () => import("Frontend/generated/flow/generated-flow-imports.js")
});

const router = {
    render() {
        return Promise.resolve();
    }
};

// @ts-ignore
function getAnchorOrigin(anchor) {
    const port = anchor.port;
    const protocol = anchor.protocol;
    const defaultHttp = protocol === 'http:' && port === '80';
    const defaultHttps = protocol === 'https:' && port === '443';
    const host = (defaultHttp || defaultHttps)
        ? anchor.hostname
        : anchor.host;
    return `${protocol}//${host}`;
}

function normalizeURL(url: URL): void | string {
    if (!url.href.startsWith(document.baseURI)) {
        return;
    }
    return '/' + url.href.slice(document.baseURI.length);
}

function extractPath(event: MouseEvent): void | string {
    if (event.defaultPrevented) {
        return;
    }

    if (event.button !== 0) {
        return;
    }

    if (event.shiftKey || event.ctrlKey || event.altKey || event.metaKey) {
        return;
    }

    let maybeAnchor = event.target;
    const path = event.composedPath
        ? event.composedPath()
        // @ts-ignore
        : (event.path || []);

    for (let i = 0; i < path.length; i++) {
        const target = path[i];
        if (target.nodeName && target.nodeName.toLowerCase() === 'a') {
            maybeAnchor = target;
            break;
        }
    }

    // @ts-ignore
    while (maybeAnchor && maybeAnchor.nodeName.toLowerCase() !== 'a') {
        // @ts-ignore
        maybeAnchor = maybeAnchor.parentNode;
    }

    // @ts-ignore
    if (!maybeAnchor || maybeAnchor.nodeName.toLowerCase() !== 'a') {
        return;
    }

    const anchor = maybeAnchor as HTMLAnchorElement;

    if (anchor.target && anchor.target.toLowerCase() !== '_self') {
        return;
    }

    if (anchor.hasAttribute('download')) {
        return;
    }

    if (anchor.hasAttribute('router-ignore')) {
        return;
    }

    if (anchor.pathname === window.location.pathname && anchor.hash !== '') {
        // @ts-ignore
        window.location.hash = anchor.hash;
        return;
    }

    // @ts-ignore
    const origin = anchor.origin || getAnchorOrigin(anchor);
    if (origin !== window.location.origin) {
        return;
    }

    return normalizeURL(new URL(anchor.href, anchor.baseURI));
}

function fireNavigated(pathname:string, search: string) {
    setTimeout(() =>
        window.dispatchEvent(new CustomEvent('vaadin-navigated', {
            detail: {
                pathname,
                search
            }
        }))
    )
}

function postpone() {
}

const prevent = () => postpone;

type RouterContainer = Awaited<ReturnType<typeof flow.serverSideRoutes[0]["action"]>>;

function Flow() {
    const ref = useRef<HTMLOutputElement>(null);
    const navigate = useNavigate();
    const blocker = useBlocker(({ currentLocation, nextLocation }) => {
        navigated.current = navigated.current || (nextLocation.pathname === currentLocation.pathname && nextLocation.search === currentLocation.search && nextLocation.hash === currentLocation.hash);
        return true;
    });
    const {pathname, search, hash} = useLocation();
    const navigated = useRef<boolean>(false);

    const containerRef = useRef<RouterContainer | undefined>(undefined);

    const navigateEventHandler = useCallback((event: MouseEvent) => {
        const path = extractPath(event);
        if (!path) {
            return;
        }

        if (event && event.preventDefault) {
            event.preventDefault();
        }

        navigated.current = false;
        navigate(path);
    }, [navigate]);

    const vaadinRouterGoEventHandler = useCallback((event: CustomEvent<URL>) => {
        const url = event.detail;
        const path = normalizeURL(url);
        if (!path) {
            return;
        }

        event.preventDefault();
        navigate(path);
    }, [navigate]);

    const vaadinNavigateEventHandler = useCallback((event: CustomEvent<{state: unknown, url: string, replace?: boolean}>) => {
        const path = '/' + event.detail.url;
        navigated.current = !event.detail.replace;
        navigate(path, { state: event.detail.state, replace: event.detail.replace});
    }, [navigate]);

    const redirect = useCallback((path: string) => {
        return (() => {
            navigate(path, {replace: true});
        });
    }, [navigate]);

    useEffect(() => {
        // @ts-ignore
        window.addEventListener('vaadin-router-go', vaadinRouterGoEventHandler);
        // @ts-ignore
        window.addEventListener('vaadin-navigate', vaadinNavigateEventHandler);

        return () => {
            // @ts-ignore
            window.removeEventListener('vaadin-router-go', vaadinRouterGoEventHandler);
            // @ts-ignore
            window.removeEventListener('vaadin-navigate', vaadinNavigateEventHandler);
        };
    }, [vaadinRouterGoEventHandler, vaadinNavigateEventHandler]);

    useEffect(() => {
        return () => {
            containerRef.current?.parentNode?.removeChild(containerRef.current);
            containerRef.current = undefined;
        };
    }, []);

    useEffect(() => {
        if (blocker.state === 'blocked') {
            if(navigated.current) {
                blocker.proceed();
                return;
            }
            const {pathname, search} = blocker.location;
            const routes = ((window as any)?.Vaadin?.routesConfig || []) as AgnosticRouteObject[];
            let matched = matchRoutes(Array.from(routes), window.location.pathname);

            // @ts-ignore
            if (matched && matched.filter(path => path.route?.element?.type?.name === Flow.name).length != 0) {
                containerRef.current?.onBeforeEnter?.call(containerRef?.current,
                    {pathname,search}, {
                        prevent() {
                            blocker.reset();
                            navigated.current = false;
                        },
                        redirect,
                        continue() {
                            blocker.proceed();
                        }
                    }, router);
                navigated.current = true;
            } else {
                Promise.resolve(containerRef.current?.onBeforeLeave?.call(containerRef?.current, {
                    pathname,
                    search
                }, {prevent}, router))
                    .then((cmd: unknown) => {
                        if (cmd === postpone && containerRef.current) {
                            containerRef.current.serverConnected = (cancel) => {
                                if (cancel) {
                                    blocker.reset();
                                } else {
                                    blocker.proceed();
                                    window.removeEventListener('click',  navigateEventHandler);
                                }
                            }
                        } else {
                            blocker.proceed();
                            window.removeEventListener('click',  navigateEventHandler);
                        }
                    });
            }
        }
    }, [blocker.state, blocker.location]);

    useEffect(() => {
        if(navigated.current) {
            navigated.current = false;
            fireNavigated(pathname,search);
            return;
        }
        flow.serverSideRoutes[0].action({pathname, search})
            .then((container) => {
                const outlet = ref.current?.parentNode;
                if (outlet && outlet !== container.parentNode) {
                    outlet.append(container);
                    window.addEventListener('click',  navigateEventHandler);
                    containerRef.current = container
                }
                return container.onBeforeEnter?.call(container, {pathname, search}, {prevent, redirect, continue() {
                        fireNavigated(pathname,search);}}, router);
            })
            .then((result: unknown) => {
                if (typeof result === "function") {
                    result();
                }
            });
    }, [pathname, search, hash]);

    return <output ref={ref} />;
}
Flow.type = 'FlowContainer'; // This is for copilot to recognize this

export const serverSideRoutes = [
    { path: '/*', element: <Flow/> },
];

export const loadComponentScript = (tag: String): Promise<void> => {
    return new Promise((resolve, reject) => {
        useEffect(() => {
            const script = document.createElement('script');
            script.src = `/web-component/${tag}.js`;
            script.onload = function() {
                resolve();
            };
            script.onerror = function(err) {
                reject(err);
            };
            document.head.appendChild(script);

            return () => {
                document.head.removeChild(script);
            }
        }, []);
    });
};

interface Properties {
    [key: string]: string;
}

export const reactElement = (tag: string, props?: Properties, onload?: () => void, onerror?: (err:any) => void) => {
    loadComponentScript(tag).then(() => onload?.(), (err) => {
        if(onerror) {
            onerror(err);
        } else {
            console.error(`Failed to load script for ${tag}.`, err);
        }
    });

    if(props) {
        return React.createElement(tag, props);
    }
    return React.createElement(tag);
};

export default Flow;

// @ts-ignore
if (import.meta.hot) {
  // @ts-ignore
  import.meta.hot.accept((newModule) => {
    if (newModule) {
      window.location.reload();
    }
  });
}
