import React, { useEffect, useRef } from 'react';
import { useUser } from '../../UserProvider/UserProvider';

const PageVideo = (props) => {
    const user = useUser();
    const {pageId, myPostId} = props;

    const videoRef = useRef(null);

    const mimeCodec = 'video/mp4; codecs="avc1.42E01E, mp4a.40.2"';
    var tempRange = "bytes=0-";

    function IsOpenSrc(e){
        const mediaSrc = e.target; 
        if(mediaSrc.sourceBuffers.length > 0) {return;}
        URL.revokeObjectURL(videoRef.current.src);
        const srcBuffer = mediaSrc.addSourceBuffer(mimeCodec);
        srcBuffer.mode = 'sequence';
        // /api/pages/${pageId}/pagePostTemp/${myPostId}
        fetch(`/api/pagePosts/page/${pageId}/postVid/${myPostId}`, {headers: {Authorization: `Bearer ${user.jwt}`, range: tempRange}, method: "GET"})
        .then((response) => 
            Promise.all([response.arrayBuffer(), response.headers])
        )
        .then(([data, headers]) => {
            tempRange = headers.get('content-range');
            console.log("from init ", tempRange);

            try{
                const InitAppendHandle = function(e){
                    var srcBuffer = e.target;
                    // srcBuffer.removeEventListener('updateend', InitAppendHandle, false);
 
                    appendNextSegment(mediaSrc);
                };
              
                srcBuffer.addEventListener('updateend', InitAppendHandle, {once : true});
                srcBuffer.appendBuffer(data);
            }
            catch(error){
                console.log("Exception while appending initial segment!", error);
            }
        })     
    }

    function appendNextSegment(mediaSrc){
        const bufferedSec = videoRef.current.buffered.end(0)-videoRef.current.buffered.start(0);
        if(mediaSrc.readyState == "closed"){console.log("closed"); return;}
        // add check condition for next segment
        // if(mediaSrc.sourceBuffers[0].updating){console.log("updating"); return;}
        videoRef.current.addEventListener('playing', fetchNextSegment(mediaSrc), {once:true});
    }

    function fetchNextSegment(mediaSrc){
        // generate next range
        const prevRange = getBytes().split("-")[1]; 
        const nextRange = prevRange.split("/");
        
        if(parseInt(nextRange[0])+1 == parseInt(nextRange[1])) {return; }
      
        var rangeStart = "bytes="+(parseInt(nextRange[0])+1).toString()+"-";
        
        // /api/pages/${pageId}/pagePostTemp/${myPostId}
        fetch(`/api/pagePosts/page/${pageId}/postVid/${myPostId}`, {headers: {Authorization: `Bearer ${user.jwt}`, range: rangeStart}, method: "GET"})
            .then((response) => 
                Promise.all([response.arrayBuffer(), response.headers])
            )
            .then(([data, headers]) => {
                tempRange = headers.get('content-range');
                // console.log(tempRange, data);
                // if(mediaSrc.readyState == 'open' && (!mediaSrc.sourceBuffers[0].updating)){
                    // if(!mediaSrc.sourceBuffers[0].updating){
                        const srcBuffer = mediaSrc.sourceBuffers[0];
                        srcBuffer.appendBuffer(data);
                    // TODO: Fetch further segment and append it
                    // videoRef.current.play().then(() => {
                    //     const tempReq = appendNextSegment(mediaSrc);

                    // })
                // }
                // mediaSrc.sourceBuffers[0].appendBuffer(data);
            }).catch((error) => {
                console.log(error);
                mediaSrc.endOfStream('network');
                return;
            })  
        
    }

    function getBytes(){return tempRange.split(" ")[1]}

    useEffect(() => {  
        if(MediaSource.isTypeSupported(mimeCodec)){
            const newMediaSource = new MediaSource();

            if(videoRef.current === null){
                setTimeout(() => {
                     const url = URL.createObjectURL(newMediaSource);
                    // const url = newMediaSource;
                    videoRef.current.src = url;
                }, 1000)
             }
             else{
                const url = URL.createObjectURL(newMediaSource);
                videoRef.current.src = url;
            }
            newMediaSource.addEventListener('sourceopen', (e) => {IsOpenSrc(e)});
        }      
    },[]);

    function displayVideo(){
        return <video ref={videoRef} className='p-1' controls={true} preload="metadata" />
    }

    return (
        <>
            {displayVideo()}
        </>
    );
};

export default PageVideo;