import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import ViewVisitUserImg from '../../Components/MediaComp/ViewVisitUserImg';
import ViewVisitUserVid from '../../Components/MediaComp/ViewVisitUserVid';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';
import relativeTime from 'dayjs/plugin/relativeTime';
import dayjs from 'dayjs';
import PostCommentModal from '../../Modals/MyPosts/Comments/PostCommentModal';

const ViewVisitUserPosts = (props) => {
    const user = useUser();
    const {eachMyPost} = props;

    const [currMedia, setCurrMedia] = useState({myFileName: "", postByte: null, type: ""});
    const [mediaType, setMediaType] = useState(null);
    const [timeCollapsed, setTimeCollapsed] = useState(null);
    const [postLiked, setPostLiked] = useState(false);
    const [show, setShow] = useState(false);

    const currPostId = eachMyPost.id;
    const firstname = eachMyPost.user.firstname;
    const lastname = eachMyPost.user.lastname;
    const createdOn = eachMyPost.creationDate;
    const postDesc = eachMyPost.description;

    useEffect(() => {
        dayjs.extend(relativeTime);
        const tempTime = dayjs(createdOn).fromNow();
        setTimeCollapsed(tempTime);
    }, [createdOn])

    function handleClose(){ setShow(false); }
    function handleShow(){ setShow(true); }

    useEffect(() => {
        requestToPath(`/api/posts/${currPostId}/isLiked`, "GET", user.jwt)
            .then((likeResponse) => {setPostLiked(true);})
            .catch((error) => {console.log(error);})
    }, [])

    useEffect(() => {
        requestToPath(`/api/posts/${currPostId}/postPhotos`, "GET", user.jwt)
            .then((ImgResponse) => {
                setMediaType(ImgResponse.type.split("/")[0]);
                setCurrMedia(ImgResponse);
            })
            .catch((error) => { console.log(error); })
    }, [currPostId])

    function displayPostMedia(){
        if(mediaType == 'image' && currMedia.path != null){
            return <ViewVisitUserImg currPostId = {currPostId}/>
        }
        else if(mediaType == 'video' && currMedia.path != null){
            return <ViewVisitUserVid currPostId = {currPostId} />
        }
    }

    function increaseLike(){
        setPostLiked(true);
        requestToPath(`/api/posts/${currPostId}/increaseLike`, "POST", user.jwt)
            .then((likeResponse) => {console.log(likeResponse); setPostLiked(true);})
            .catch((error) => {console.log(error);})
    }

    function decreaseLike(){
        setPostLiked(false);
        requestToPath(`/api/posts/${currPostId}/decreaseLike`, "DELETE", user.jwt)
            .then((likeResponse) => {setPostLiked(false);})
            .catch((error) => {console.log(error);})
    }

    function displayLikeBtn(){
        if(postLiked){ return <Button className='flex-fill me-1' onClick={() => {decreaseLike();}}>Like</Button> }
        return <Button className='flex-fill me-1' variant='secondary' onClick={() => {increaseLike()}}>Like</Button>
    }

    function displayCommentBtn(){
        return <Button className='flex-fill ms-1' variant='secondary' onClick={() => {handleShow()}}>Comments</Button>
    }

    return (
        <div key={eachMyPost.id} className="mt-5 d-flex flex-column justify-content-center align-items-center">
            <Card className='custom-postCard'>  
                <Card.Body>
                    <div className='d-flex justify-content-between'>
                        <div>
                            <Card.Title>{firstname} {lastname}</Card.Title>
                            <Card.Subtitle className="mb-2 text-muted">{timeCollapsed}</Card.Subtitle>
                        </div>
                    </div>
                    <Card.Text> {postDesc} </Card.Text>
                </Card.Body>

                {displayPostMedia()}
                <Card.Footer className='d-flex'>
                    {displayLikeBtn()}
                    {displayCommentBtn()}
                </Card.Footer>
                <PostCommentModal postId={currPostId} show={show} emitHandleClose={handleClose} />
            </Card>
        </div>
    );
};

export default ViewVisitUserPosts;