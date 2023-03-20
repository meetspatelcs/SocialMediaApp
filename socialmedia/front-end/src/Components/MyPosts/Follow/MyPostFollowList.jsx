import React from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../../../UserProvider/UserProvider';

const MyPostFollowList = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const {currPage, visitUser} = props;
    const navigate = useNavigate();
    const pageName = currPage.compName;

    function visitPage(){ navigate(`/users/${userId}/pages/page/${currPage.id}`); }
    function handleButton(){
        if(visitUser != null){
            return <div className='text-center'>
                <Button style={{width: "75px"}} className='mt-1' size='sm' variant="primary" onClick={() => {visitPage()}}>Visit</Button>
            </div>
        }
        else{
            return <div className='text-center'><Button style={{width: "75px"}} className='mt-1' size='sm' variant="secondary">Unfollow</Button><br/>
                <Button style={{width: "75px"}} className='mt-1' size='sm' variant="primary" onClick={() => {visitPage()}}>Visit</Button></div>
        }
    }

    return (
        <div>
                <Card>
                <Card.Body>
                    <Card.Title>{pageName}</Card.Title>
                    {handleButton()}        
                </Card.Body>
            </Card>
        </div>
    );
};

export default MyPostFollowList;