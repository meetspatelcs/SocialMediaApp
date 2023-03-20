import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';
import relativeTime from 'dayjs/plugin/relativeTime';
import dayjs from 'dayjs';
import UserImage from './UserMedia/UserImage';
import UserVideo from './UserMedia/UserVideo';
import PostCommentModal from '../Modals/MyPosts/Comments/PostCommentModal';
    

const ViewPostsComp = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();
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

    useEffect(() => {
        requestToPath(`/api/posts/${currPostId}/isLiked`, "GET", user.jwt)
            .then((likeResponse) => {setPostLiked(true);})
            .catch((error) => {console.log(error);})
    }, [])

    function pageRefresh(){ window.location.reload(); }

    function handleClose(){ setShow(false); }
    function handleShow(){ setShow(true); }

    function removePostRequest(delId){
        requestToPath(`/api/posts/${delId}`, "DELETE", user.jwt)
            .then((postResponse) => { console.log(postResponse);})
            .catch((error) => {console.log(error);})
    }

    useEffect(() => {
        requestToPath(`/api/posts/${currPostId}/postPhotos`, "GET", user.jwt)
            .then((ImgResponse) => {
                setMediaType(ImgResponse.type.split("/")[0]);
                setCurrMedia(ImgResponse);
            })
            .catch((error) => {console.log(error);})
    }, [])

    function displayMedia(){
        if(mediaType == 'image' && currMedia.path != null){
            return <UserImage postId={currPostId} />
        }
        else if(mediaType == 'video' && currMedia.path != null){
            return <UserVideo postId={currPostId} />
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
                        <div>
                            <Button variant='outline-primary me-1' onClick={() => {navigate(`/users/${userId}/myPosts/${currPostId}/edit`)}}>Edit</Button> 
                            <Button variant='outline-danger' onClick={() => {removePostRequest(currPostId); pageRefresh();}}>Remove</Button>
                        </div>
                    </div>
                    
                    <Card.Text> {postDesc} </Card.Text>
                </Card.Body>
                
                {displayMedia()}

                <Card.Footer className='d-flex'>
                    {displayLikeBtn()}
                    {displayCommentBtn()}
                </Card.Footer>

                <PostCommentModal postId={currPostId} show={show} emitHandleClose={handleClose} />
            </Card>
        </div>
    );
};

export default ViewPostsComp;