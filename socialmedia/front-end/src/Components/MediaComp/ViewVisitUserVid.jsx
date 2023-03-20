import React, { useEffect, useRef } from 'react';
import { useUser } from '../../UserProvider/UserProvider';

const ViewVisitUserVid = (props) => {
    const user = useUser();
    const {currPostId} = props;
    const mimeCodec = 'video/mp4; codecs="avc1.42E01E, mp4a.40.2"';
    var tempRange = "bytes=0-";

    const videoRef = useRef(null);

    function IsOpenSrc(e){
        const mediaSrc = e.target; 
        if(mediaSrc.sourceBuffers.length > 0) {return;}
        URL.revokeObjectURL(videoRef.current.src);
        const srcBuffer = mediaSrc.addSourceBuffer(mimeCodec);
        srcBuffer.mode = 'sequence'
        fetch(`/api/posts/${currPostId}/postVideos`, {headers: {Authorization: `Bearer ${user.jwt}`, range: tempRange}, method: "GET"})
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

    function fetchNextSegment(mediaSrc){
        // generate next range
       
        const prevRange = getBytes().split("-")[1]; 
        const nextRange = prevRange.split("/");
        
        console.log(nextRange);
        if(parseInt(nextRange[0])+1 == parseInt(nextRange[1])) {return;
            
        }
      
        var rangeStart = "bytes="+(parseInt(nextRange[0])+1).toString()+"-";
        console.log(rangeStart);

        fetch(`/api/posts/${currPostId}/postVideos`, {headers: {Authorization: `Bearer ${user.jwt}`, range: rangeStart}, method: "GET"})
            .then((response) => 
                Promise.all([response.arrayBuffer(), response.headers])
            )
            .then(([data, headers]) => {
                tempRange = headers.get('content-range');
                // console.log(tempRange, data);
                // if(mediaSrc.readyState == 'open' && (!mediaSrc.sourceBuffers[0].updating)){
                    console.log("in next ap");
                    console.log(mediaSrc);
                    // if(!mediaSrc.sourceBuffers[0].updating){
                        const srcBuffer = mediaSrc.sourceBuffers[0];
                        console.log(srcBuffer, "from");
                        srcBuffer.appendBuffer(data);
                    // TODO: Fetch further segment and append it
                    videoRef.current.play().then(() => {
                        const tempReq = appendNextSegment(mediaSrc);

                    })
                // }
                // mediaSrc.sourceBuffers[0].appendBuffer(data);
            }).catch((error) => {
                console.log(error);
                mediaSrc.endOfStream('network');
                return;
            })  
    }
     
    function getBytes(){
        console.log(tempRange);
        return tempRange.split(" ")[1];
    }

    function appendNextSegment(mediaSrc){
        
        console.log("next", mediaSrc);
        const bufferedSec = videoRef.current.buffered.end(0)-videoRef.current.buffered.start(0);
        console.log("video is ready to play till: ", bufferedSec);
        if(mediaSrc.readyState == "closed"){console.log("closed"); return;}
        // add check condition for next segment
        // if(mediaSrc.sourceBuffers[0].updating){console.log("updating"); return;}

        console.log("check point");
        videoRef.current.addEventListener('playing', fetchNextSegment(mediaSrc), {once:true});
    }

    useEffect(() => {
        
        if(MediaSource.isTypeSupported(mimeCodec)){
            const newMediaSource = new MediaSource();

            if(videoRef.current === null){
                setTimeout(() => {
                     const url = URL.createObjectURL(newMediaSource);
                    // const url = newMediaSource;
                    videoRef.current.src = url;
                    console.log(videoRef.current.src);
                }, 1000)
             }
             else{
                const url = URL.createObjectURL(newMediaSource);
                videoRef.current.src = url;
                console.log(videoRef.current.src, "from else");
            }
            newMediaSource.addEventListener('sourceopen', (e) => {IsOpenSrc(e)});

        }      
    },[])

    return (
        <>
            <video ref={videoRef} className='p-1' controls={true} preload="metadata" /> 
        </>
        
    );
};

export default ViewVisitUserVid;