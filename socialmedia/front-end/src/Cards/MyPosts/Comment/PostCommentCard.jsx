import React, { useEffect, useState } from 'react';
import { Card } from 'react-bootstrap';
import relativeTime from 'dayjs/plugin/relativeTime';
import dayjs from 'dayjs';

const PostCommentCard = (props) => {

    const {description, createdOn, firstname, lastname} = props;
    const [timeCollapsed, setTimeCollapsed] = useState(null);

    useEffect(() => {
        dayjs.extend(relativeTime);
        const tempTime = dayjs(createdOn).fromNow();
        setTimeCollapsed(tempTime);
    }, [createdOn])

    return (
        <Card style={{width: "99%",border: "none", backgroundColor: "whitesmoke"}}>
            {/* <Card.Img variant="top" src="holder.js/100px180" /> */}
            <Card.Body>
                <Card.Title>{firstname} {lastname}</Card.Title>
                <Card.Subtitle className="mb-2 text-muted" style={{fontSize: "12px"}}>{timeCollapsed}</Card.Subtitle>
                <Card.Text className='mt-3'>{description}</Card.Text>
                {/* <Button variant="primary">Go somewhere</Button> */}
            </Card.Body>
        </Card>
    );
};

export default PostCommentCard;