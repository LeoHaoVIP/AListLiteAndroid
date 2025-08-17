ARG BASE_IMAGE_TAG=base
FROM ghcr.io/openlistteam/openlist-base-image:${BASE_IMAGE_TAG}

ARG TARGETPLATFORM
ARG INSTALL_FFMPEG=false
ARG INSTALL_ARIA2=false
LABEL MAINTAINER="OpenList"

WORKDIR /opt/openlist/

COPY --chmod=755 /build/${TARGETPLATFORM}/openlist ./
COPY --chmod=755 entrypoint.sh /entrypoint.sh
RUN /entrypoint.sh version

ENV PUID=0 PGID=0 UMASK=022 RUN_ARIA2=${INSTALL_ARIA2}
VOLUME /opt/openlist/data/
EXPOSE 5244 5245
CMD [ "/entrypoint.sh" ]