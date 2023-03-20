import React, { useEffect, useRef } from 'react';
import { Card } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import { useUser } from '../../UserProvider/UserProvider';

const OtherAllVid = (props) => {
    const user = useUser();
    const {currVid} = props;
    const {pageId} = useParams();
    const myPostId = currVid.id;
    const videoRef = useRef(null);
    const mimeCodec = 'video/mp4; codecs="avc1.42E01E, mp4a.40.2"';
    var tempRange = "bytes=0-";

    function IsOpenSrc(e){
        const mediaSrc = e.target; 

        if(mediaSrc.sourceBuffers.length > 0) {return;}
        URL.revokeObjectURL(videoRef.current.src);
        
        const srcBuffer = mediaSrc.addSourceBuffer(mimeCodec);
        srcBuffer.mode = 'sequence'
        // /api/pages/${pageId}/pagePostTemp/${myPostId}
        fetch(`/api/pagePosts/page/${pageId}/postVid/${myPostId}`, {headers: {Authorization: `Bearer ${user.jwt}`, range: tempRange}, method: "GET"})
            .then((response) => 
                Promise.all([response.arrayBuffer(), response.headers])
            )
            .then(([data, headers]) => {
                tempRange = headers.get('content-range');

                try{
                    const InitAppendHandle = function(e){
                        var srcBuffer = e.target;
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
     
        fetchNextSegment(mediaSrc);
    }

    function fetchNextSegment(mediaSrc){
        
        const prevRange = getBytes().split("-")[1]; 
        const nextRange = prevRange.split("/");
        
   
        if(parseInt(nextRange[0])+1 == parseInt(nextRange[1])) {return;}
      
            var rangeStart = "bytes="+(parseInt(nextRange[0])+1).toString()+"-";

            // /api/pages/${pageId}/pagePostTemp/${myPostId}
            fetch(`/api/pagePost/page/${pageId}/postVid/${myPostId}`, {headers: {Authorization: `Bearer ${user.jwt}`, range: rangeStart}, method: "GET"})
                .then((response) => 
                    Promise.all([response.arrayBuffer(), response.headers])
                )
                .then(([data, headers]) => {
                    tempRange = headers.get('content-range');
    
                        const srcBuffer = mediaSrc.sourceBuffers[0];
                        srcBuffer.appendBuffer(data);
                        // TODO: Fetch further segment and append it
                        testFunc(mediaSrc);
                }).catch((error) => {
                    console.log(error);
                    mediaSrc.endOfStream('network');
                    return;
                })  
        
    }

    function testFunc(mediaSrc){
        // appendNextSegment(mediaSrc);

        videoRef.current.addEventListener('playing', (e) => {

            const bufferedSec = videoRef.current.buffered.end(0)-videoRef.current.buffered.start(0);
            console.log(bufferedSec," " ,videoRef.current.currentTime);

            appendNextSegment(mediaSrc);
        })

        // videoRef.current.addEventListener('waiting', (e) => {
        //     if(videoRef.current.play)
        //         videoRef.current.pause()

        //     appendNextSegment(mediaSrc);
        // })

        // videoRef.current.addEventListener('canplaythrough', (e) => {
        //     if(videoRef.current.pause)
        //         videoRef.current.play();
        // })
    }

    function getBytes(){
        return tempRange.split(" ")[1];
    }

    useEffect(() => {
        
        if(MediaSource.isTypeSupported(mimeCodec)){
            const newMediaSource = new MediaSource();

            if(videoRef.current === null){
                setTimeout(() => {
                     const url = URL.createObjectURL(newMediaSource);
                    // const url = newMediaSource;
                    videoRef.current.src = url;
                    // console.log(videoRef.current.src);
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

    function displayVid(){
        return <video className='otherAllVid-vid' ref={videoRef} controls={true} preload="metadata" />
    }

    return (
        <Card className='d-flex align-items-center' style={{border: "none"}}>
            <Card.Body>
                {displayVid()}
            </Card.Body>
        </Card>
    );
};

export default OtherAllVid;