import React, { useEffect, useRef, useState } from 'react';
import { useUser } from '../../UserProvider/UserProvider';

const UserVideo = (props) => {
    const user = useUser();
    const {postId} = props;
    const videoRef = useRef(null);
    const endTHRESHOLD = 0.1;
    const mimeCodec = 'video/mp4; codecs="avc1.42E01E, mp4a.40.2"';

    const [sourceOpen, setSourceOpen] = useState(false);
    const [mediaSource, setMediaSource] = useState(null);
    const [sourceBuffer, setSourceBuffer] = useState(null);
    const [myrange, setMyRange] = useState('bytes=0-');
    const [totalByte, setTotalByte] = useState(null);
    const [queue, setQueue] = useState([]);
    const [preloadBuffer, setPreloadBufferr] = useState(false);
    const [preloadLen, setPreloadLen] = useState(0);

    function initSourceBuffer() {
        if (sourceOpen && mediaSource && mediaSource.sourceBuffers.length === 0) {
            const buffer = mediaSource.addSourceBuffer(mimeCodec);
            setSourceBuffer(buffer);
        }
    }
      
    function onSourceOpen() { setSourceOpen(true); }
  
    function fetchAndQueueSegment(){
        fetch(`/api/posts/${postId}/postVideos`, { headers: { Authorization: `Bearer ${user.jwt}`, range: myrange }, method: 'GET' })
            .then((response) => {
                if(response.status == 200){
                setPreloadBufferr(false);
                setPreloadLen(999);
                }            
                return Promise.all([response.arrayBuffer(), response.headers])})
            .then(([data, headers]) => {
                const rangeVal = headers.get('content-range');
                const startByte = rangeVal.substring(rangeVal.indexOf('-')+1).split("/")[0];
                const newrange = `bytes=${parseInt(startByte)+1}-`;
                setMyRange(newrange);

                setQueue([...queue, data]);
                const tempLen = preloadLen + 1;
                setPreloadLen(tempLen); 
            })
            .catch((error) => {
                mediaSource.endOfStream('network');
            });
    }
  
    function fetchAndAppendSegment() {
        fetch(`/api/posts/${postId}/postVideos`, { headers: { Authorization: `Bearer ${user.jwt}`, range: myrange }, method: 'GET' })
            .then((response) => Promise.all([response.arrayBuffer(), response.headers]))
            .then(([data, headers]) => {
            const rangeVal = headers.get('content-range');
            const startByte = rangeVal.substring(rangeVal.indexOf('-')+1).split("/")[0];
            setTotalByte(rangeVal.substring(rangeVal.indexOf('-')+1).split("/")[1]);
            const newrange = `bytes=${parseInt(startByte)+1}-`;
            setMyRange(newrange);

            sourceBuffer.appendBuffer(data);
            setPreloadBufferr(true);
            })
            .catch((error) => {
                mediaSource.endOfStream('network');
            });
    }

    function handleSegment(){
        if(queue.length > 0){
            const nextData = queue.shift();
            if(sourceBuffer.updating){return;}
            sourceBuffer.appendBuffer(nextData);

            const tempLen = preloadLen - 1;
            setPreloadLen(tempLen);        
        }
    }
  
    useEffect(() => {
        if (MediaSource.isTypeSupported(mimeCodec)) {
            const newMediaSource = new MediaSource();
            newMediaSource.addEventListener('sourceopen', onSourceOpen);
            setMediaSource(newMediaSource);
        }
    }, []);
    
    useEffect(() => {
        if (mediaSource && !videoRef.current.src) {
            const url = URL.createObjectURL(mediaSource);
            videoRef.current.src = url;
        }
    }, [mediaSource]);
    
    useEffect(() => {
        initSourceBuffer();
    }, [sourceOpen, mediaSource]);
    
    useEffect(() => {
        if (sourceBuffer) {
            fetchAndAppendSegment();
        }
    }, [sourceBuffer]);
  
    useEffect(() => {
        if(preloadBuffer && preloadLen < 8 && preloadLen > -1){
            fetchAndQueueSegment();
        }
    }, [preloadBuffer, preloadLen, queue])
  
    function handlePause(){
        if(videoRef.current && !videoRef.current.paused){ videoRef.current.pause(); }
    }
  
    function handlePlay(){
        if(videoRef.current && videoRef.current.paused){ videoRef.current.play(); }
    }
  
    function handleTimeUpdate(){
        if(sourceBuffer.buffered.length > 0){
            const currentTime = videoRef.current.currentTime;
            const bufferDuration = sourceBuffer.buffered.end(0) - sourceBuffer.buffered.start(0);
            const bufferThreshold = bufferDuration * 0.7;

            if(currentTime >= sourceBuffer.buffered.start(0) + bufferThreshold){ handleSegment(); }
            if(currentTime >= videoRef.current.duration-endTHRESHOLD || videoRef.current.ended){ videoRef.current.pause();}
        }
    }

    function displayVideo(){
        return <video ref={videoRef} autoPlay={false} onTimeUpdate={handleTimeUpdate} controls={true} onPause={handlePause} onPlay={handlePlay}  preload='auto' />
    }

    return (
        <> {displayVideo()} </>
    );
};

export default UserVideo;